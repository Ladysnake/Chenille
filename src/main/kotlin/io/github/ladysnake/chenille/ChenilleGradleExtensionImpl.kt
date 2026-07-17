/*
 * Chenille
 * Copyright (C) 2022-2026 Ladysnake
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; If not, see <https://www.gnu.org/licenses>.
 */
package io.github.ladysnake.chenille

import io.github.ladysnake.chenille.api.ArtifactLifecycle
import io.github.ladysnake.chenille.api.ChenilleGradleExtension
import io.github.ladysnake.chenille.api.ChenilleRepositoryHandler
import io.github.ladysnake.chenille.api.PublishingConfiguration
import io.github.ladysnake.chenille.api.TestmodConfiguration
import io.github.ladysnake.chenille.helpers.CurseGradleHelper
import io.github.ladysnake.chenille.helpers.GithubReleaseHelper
import io.github.ladysnake.chenille.helpers.LicenserHelper
import io.github.ladysnake.chenille.helpers.MavenHelper
import io.github.ladysnake.chenille.helpers.ModrinthHelper
import net.fabricmc.loom.api.LoomGradleExtensionAPI
import net.fabricmc.loom.task.RemapJarTask
import org.gradle.api.Action
import org.gradle.api.Task
import org.gradle.api.UnknownTaskException
import org.gradle.api.provider.Provider
import org.gradle.api.resources.TextResource
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.TaskProvider
import org.gradle.api.tasks.bundling.Jar
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.internal.extensions.stdlib.capitalized
import java.io.File
import java.net.URI
import java.net.URL

open class ChenilleGradleExtensionImpl(private val project: ChenilleProject) : ChenilleGradleExtension {
    override var changelogFile: File by defaulted { project.file("changelog.md") }

    override var javaVersion: Int by defaulted { 17 } withListener { value ->
        project.tasks.withType(JavaCompile::class.java).configureEach {
            options.release.set(value)
        }
    }

    override var modVersion: String by defaulted { project.version.toString() }

    override var license: String? by defaulted<String?> { null } withListener {
        LicenserHelper.configure(project, it?.uppercase(), customLicense)
    }

    override var customLicense: Any? by defaulted<Any?> { null } withListener {
        LicenserHelper.configure(project, license?.uppercase(), it)
    }

    override var displayName: String by defaulted {
        project.name.split("-").joinToString(" ") { it.capitalized() }
    }

    override var owners: String by defaulted {
        project.group.toString().split('.').last().capitalized()
    }

    override var github: URL? by defaulted { URI("https://github.com/$owners/$displayName").toURL() }

    override var changelogUrl: URL? by defaulted { URI("$github/blob/$modVersion/changelog.md").toURL() }

    override val repositories: ChenilleRepositoryHandler
        get() = ChenilleRepositoryHandlerImpl(this.project.repositories)

    override fun repositories(action: Action<ChenilleRepositoryHandler>) {
        action.execute(this.repositories)
    }

    override fun repositories(action: ChenilleRepositoryHandler.() -> Unit) {
        this.repositories.action()
    }

    override fun configurePublishing(action: Action<PublishingConfiguration>) {
        val cfg = object: PublishingConfiguration {
            var curseforge = false
            var github = false
            var modrinth = false
            var ladysnakeArtifactLifecycle: ArtifactLifecycle? = null

            override var mainArtifact: Any = if (project.usesRemapLoom()) {
                project.tasks.named("remapJar", RemapJarTask::class.java)
            } else {
                project.tasks.named("jar", Jar::class.java)
            }.flatMap { it.archiveFile }

            override var fabricCompatible = true
            override var quiltCompatible = true
            override var neoforgeCompatible = false

            override fun withLadysnakeMaven(lifecycle: ArtifactLifecycle) {
                ladysnakeArtifactLifecycle = lifecycle
            }

            override fun withGithubRelease() {
                github = true
            }

            override fun withCurseforgeRelease() {
                curseforge = true
            }

            override fun withModrinthRelease() {
                modrinth = true
            }
        }

        action.execute(cfg)

        MavenHelper.configureDefaults(project, cfg.ladysnakeArtifactLifecycle)

        val checkGitStatus: TaskProvider<CheckGitTask> =
            project.tasks.register("checkGitStatus", CheckGitTask::class.java, project)

        val release: TaskProvider<Task> = project.tasks.register("release") {
            group = "publishing"
            description = "Releases a new version to Maven, Github, Curseforge and Modrinth"
            dependsOn(checkGitStatus)
        }

        fun configureReleaseSubtask(name: String) {
            try {
                val subtask = project.tasks.named(name).configure {
                    mustRunAfter(checkGitStatus)
                }
                release.configure {
                    dependsOn(subtask)
                }
            } catch (_: UnknownTaskException) {
                release.configure {
                    onlyIf("NO-TASKS") { false }
                    doFirst { project.logger.warn("Task $name not found; skipping it for release") }
                }
            }
        }

        if (cfg.ladysnakeArtifactLifecycle != null) {
            configureReleaseSubtask("publish")
        }

        if (cfg.curseforge) {
            project.pluginManager.apply("net.darkhax.curseforgegradle")
            CurseGradleHelper.configureDefaults(project, cfg)
            configureReleaseSubtask("curseforge")
        }

        if (cfg.github) {
            GithubReleaseHelper.configureDefaults(project, cfg)
            configureReleaseSubtask("githubRelease")
        }

        if (cfg.modrinth) {
            ModrinthHelper.configureDefaults(project, cfg)
            configureReleaseSubtask("modrinth")
        }
    }

    override fun licenseHeader(license: String): Provider<TextResource> = project.provider {
        project.resources.text.fromUri(
            ChenilleGradlePlugin::class.java.getResource("/license_headers/${license}.txt")
                ?: throw IllegalArgumentException("$license is not a recognized license header")
        )
    }

    override fun configureTestmod() {
        configureTestmod {}
    }

    override fun configureTestmod(action: Action<TestmodConfiguration>) {
        val cfg = object: TestmodConfiguration {
            var baseTestRuns: Boolean = false
            var dependencyConfiguration: Boolean = false
            override fun withBaseTestRuns() {
                baseTestRuns = true
            }
            override fun withDependencyConfiguration() {
                if (!project.usesRemapLoom()) {
                    error("Dependency configuration helper is only available for projects with remapped configurations (before MC 26.1)")
                }
                dependencyConfiguration = true
            }
        }.also { action.execute(it) }

        val sourceSets = project.extensions.getByType(SourceSetContainer::class.java)
        val main = sourceSets.getByName("main")
        val testmodSourceSet = sourceSets.create("testmod") {
            compileClasspath += main.compileClasspath
            runtimeClasspath += main.runtimeClasspath
        }

        project.dependencies.add("testmodImplementation", main.output)

        project.extensions.configure<LoomGradleExtensionAPI>("loom") {
            if (cfg.dependencyConfiguration) {
                createRemapConfigurations(testmodSourceSet)
            }

            runs {
                if (cfg.baseTestRuns) {
                    create("testmodClient") {
                        client()
                        name("Testmod Client")
                        source(testmodSourceSet)
                    }
                    create("testmodServer") {
                        server()
                        name("Testmod Server")
                        source(testmodSourceSet)
                    }
                }
                create("gametest") {
                    server()
                    name("Game Test")
                    source(testmodSourceSet)
                    // Enable the gametest runner regardless of the framework
                    vmArg("-Dquilt.game_test=true")
                    vmArg("-Dfabric-api.gametest")
                    vmArg("-Dfabric-api.gametest.report-file=${project.layout.buildDirectory.asFile.get()}/junit.xml")
                    runDir("build/gametest")
                }
                create("autoTestServer") {
                    server()
                    configName = "Auto Test Server"
                    source(testmodSourceSet)
                    property("quilt.auto_test")
                    programArg("--nogui")
                }
                project.tasks.named("check").configure {
                    dependsOn(project.tasks.named("runGametest"))
                }
            }
        }
    }
}
