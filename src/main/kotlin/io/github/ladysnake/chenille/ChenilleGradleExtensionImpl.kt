/*
 * Chenille
 * Copyright (C) 2022 Ladysnake
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

import com.google.common.collect.ImmutableList
import io.github.ladysnake.chenille.api.ChenilleGradleExtension
import io.github.ladysnake.chenille.api.ChenilleRepositoryHandler
import io.github.ladysnake.chenille.api.PublishingConfiguration
import io.github.ladysnake.chenille.api.TestmodConfiguration
import io.github.ladysnake.chenille.helpers.ArtifactoryHelper
import io.github.ladysnake.chenille.helpers.CurseGradleHelper
import io.github.ladysnake.chenille.helpers.GithubReleaseHelper
import io.github.ladysnake.chenille.helpers.LicenserHelper
import io.github.ladysnake.chenille.helpers.MavenHelper
import io.github.ladysnake.chenille.helpers.ModrinthHelper
import net.fabricmc.loom.LoomGradleExtension
import net.fabricmc.loom.configuration.RemappedConfigurationEntry
import net.fabricmc.loom.util.Constants
import org.cadixdev.gradle.licenser.LicenseExtension
import org.gradle.BuildListener
import org.gradle.BuildResult
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.UnknownTaskException
import org.gradle.api.initialization.Settings
import org.gradle.api.invocation.Gradle
import org.gradle.api.provider.Provider
import org.gradle.api.resources.TextResource
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.TaskProvider
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.configurationcache.extensions.capitalized
import sun.misc.Unsafe
import java.io.File
import java.net.URL

open class ChenilleGradleExtensionImpl(private val project: ChenilleProject) : ChenilleGradleExtension {
    override var changelogFile: File by defaulted { project.file("changelog.md") }

    override var javaVersion: Int by defaulted { 16 } withListener { value ->
        project.tasks.withType(JavaCompile::class.java).configureEach {
            it.options.release.set(value)
        }
    }

    override var modVersion: String by defaulted { project.version.toString() }

    override var license: String? by defaulted<String?> { null } withListener {
        LicenserHelper.configure(project, it?.uppercase())
    }

    override var displayName: String by defaulted { project.name.capitalized() } withListener { value ->
        project.extensions.findByType(LicenseExtension::class.java)?.let {
            it.properties { ext -> ext["projectDisplayName"] = value }
        }
    }

    override var owners: String by defaulted {
        project.group.toString().split('.').last().capitalized()
    } withListener { value ->
        project.extensions.findByType(LicenseExtension::class.java)?.let {
            it.properties { ext -> ext["projectOwners"] = value }
        }
    }

    override var github: URL? by defaulted { URL("https://github.com/$owners/$displayName") }

    override var changelogUrl: URL? by defaulted { URL("$github/blob/$modVersion/changelog.md") }

    override val repositories: ChenilleRepositoryHandler
        get() = ChenilleRepositoryHandlerImpl(this.project.repositories)

    override fun repositories(action: Action<ChenilleRepositoryHandler>) {
        action.execute(this.repositories)
    }

    override fun repositories(action: ChenilleRepositoryHandler.() -> Unit) {
        this.repositories.action()
    }

    override fun configurePublishing() {
        configurePublishing {}
    }

    override fun configurePublishing(action: Action<PublishingConfiguration>) {
        val cfg = object: PublishingConfiguration {
            var artifactory = false
            var curseforge = false
            var github = false
            var modrinth = false

            override fun withArtifactory() {
                artifactory = true
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

        MavenHelper.configureDefaults(project)

        val checkGitStatus: TaskProvider<CheckGitTask> =
            project.tasks.register("checkGitStatus", CheckGitTask::class.java, project)

        val release: TaskProvider<Task> = project.tasks.register("release") {
            it.group = "publishing"
            it.description = "Releases a new version to Maven, Github, Curseforge and Modrinth"
            it.dependsOn(checkGitStatus)
        }

        fun configureReleaseSubtask(name: String) {
            try {
                val subtask = project.tasks.named(name)
                subtask.configure { it.mustRunAfter(checkGitStatus) }
                release.configure { it.dependsOn(subtask) }
            } catch (_: UnknownTaskException) {
                release.configure {
                    it.doFirst { project.logger.warn("Task $name not found; skipping it for release") }
                }
            }
        }

        if (cfg.artifactory) {
            ArtifactoryHelper.configureDefaults(project)
            configureReleaseSubtask("artifactoryPublish")
        }

        if (cfg.curseforge) {
            CurseGradleHelper.configureDefaults(project)
            configureReleaseSubtask("curseforge")
        }

        if (cfg.github) {
            GithubReleaseHelper.configureDefaults(project)
            configureReleaseSubtask("githubRelease")
        }

        if (cfg.modrinth) {
            ModrinthHelper.configureDefaults(project)
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
                dependencyConfiguration = true
            }
        }.also { action.execute(it) }

        val sourceSets = project.extensions.getByType(SourceSetContainer::class.java)
        val main = sourceSets.getByName("main")
        val testmodSourceSet = sourceSets.create("testmod") { testmod ->
            testmod.compileClasspath += main.compileClasspath
            testmod.runtimeClasspath += main.runtimeClasspath
        }

        project.dependencies.add("testmodImplementation", main.output)

        project.extensions.configure<LoomGradleExtension>("loom") { loom ->
            loom.runs {
                if (cfg.baseTestRuns) {
                    it.create("testmodClient") { run ->
                        run.client()
                        run.name("Testmod Client")
                        run.source(testmodSourceSet)
                    }
                    it.create("testmodServer") { run ->
                        run.server()
                        run.name("Testmod Server")
                        run.source(testmodSourceSet)
                    }
                }
                it.create("gametest") { run ->
                    run.server()
                    run.name("Game Test")
                    run.source(testmodSourceSet)
                    // Enable the gametest runner
                    run.vmArg("-Dfabric-api.gametest")
                    run.vmArg("-Dfabric-api.gametest.report-file=${project.buildDir}/junit.xml")
                    run.runDir("build/gametest")
                }
                it.create("autoTestServer") { run ->
                    run.server()
                    run.name("Auto Test Server")
                    run.source(testmodSourceSet)
                    run.vmArg("-Dfabric.autoTest")
                }
                project.tasks.named("check") { check ->
                    check.dependsOn(project.tasks.named("runGametest"))
                }
            }
            if (cfg.dependencyConfiguration) {
                val modTestImplementationMapped =
                    loom.createLazyConfiguration("modTestImplementationMapped") { it.isTransitive = false }
                loom.createLazyConfiguration("modTestImplementation") { modTestImplementation ->
                    val remappedConfigurationEntry = RemappedConfigurationEntry(
                        modTestImplementation.name,
                        "testmodImplementation",
                        true,
                        true,
                        RemappedConfigurationEntry.PublishingMode.NONE
                    )
                    assert(modTestImplementationMapped.name == remappedConfigurationEntry.remappedConfiguration)
                    awfulDisgustingHack(project, remappedConfigurationEntry)
                    project.configurations.getByName("testmodCompileClasspath")
                        .extendsFrom(modTestImplementationMapped.get())
                    project.configurations.getByName("testmodRuntimeClasspath")
                        .extendsFrom(modTestImplementationMapped.get())
                }
            }
        }
    }

    private fun awfulDisgustingHack(project: Project, remappedConfigurationEntry: RemappedConfigurationEntry) {
        val oldModCompileEntries = Constants.MOD_COMPILE_ENTRIES
        val newModCompileEntries = ImmutableList.builder<RemappedConfigurationEntry>().addAll(oldModCompileEntries)
            .add(remappedConfigurationEntry).build()
        val zlorg = Unsafe::class.java.getDeclaredField("theUnsafe").also { it.isAccessible = true }[null] as Unsafe
        val f = Constants::class.java.getField("MOD_COMPILE_ENTRIES")
        val base = zlorg.staticFieldBase(f)
        val offset = zlorg.staticFieldOffset(f)
        assert(zlorg.getObject(base, offset) == oldModCompileEntries)
        zlorg.putObject(base, offset, newModCompileEntries)
        assert(zlorg.getObject(base, offset) == newModCompileEntries)

        project.gradle.addListener(object : BuildListener {
            override fun settingsEvaluated(settings: Settings) {}

            override fun projectsLoaded(gradle: Gradle) {}

            override fun projectsEvaluated(gradle: Gradle) {}

            override fun buildFinished(result: BuildResult) {
                zlorg.putObject(base, offset, oldModCompileEntries)    // Got to clean up afterward :)
            }
        })
    }
}
