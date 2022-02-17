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

import io.github.ladysnake.chenille.api.ChenilleGradleExtension
import io.github.ladysnake.chenille.api.RepositoryHandlerChenilleExtension
import io.github.ladysnake.chenille.helpers.ArtifactoryHelper
import io.github.ladysnake.chenille.helpers.CurseGradleHelper
import io.github.ladysnake.chenille.helpers.GithubReleaseHelper
import io.github.ladysnake.chenille.helpers.LicenserHelper
import io.github.ladysnake.chenille.helpers.ModrinthHelper
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.UnknownTaskException
import org.gradle.api.artifacts.ConfigurationContainer
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.tasks.TaskProvider
import java.lang.reflect.Modifier
import java.util.*
import java.util.function.Function
import java.util.stream.Collectors

@Suppress("unused") // Plugin entrypoint duh
class ChenilleGradlePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.plugins.apply("fabric-loom")

        val project = ChenilleProject(target)

        target.plugins.findPlugin("com.jfrog.artifactory")?.run { ArtifactoryHelper(project).configureDefaults() }
        target.plugins.findPlugin("org.cadixdev.licenser")?.run { LicenserHelper(project).configureDefaults() }
        target.plugins.findPlugin("com.github.breadmoirai.github-release")?.run { GithubReleaseHelper(project).configureDefaults() }
        target.plugins.findPlugin("com.matthewprenger.cursegradle")?.run { CurseGradleHelper(project).configureDefaults() }
        target.plugins.findPlugin("com.modrinth.minotaur")?.run { ModrinthHelper(project).configureDefaults() }
        project.extensions.create(ChenilleGradleExtension::class.java, "chenille", ChenilleGradleExtensionImpl::class.java, project)

        configureReleaseTask(project)

        setupConfigurations(project.configurations)
        setupRepositoryExtensions(target)
    }

    private fun configureReleaseTask(project: ChenilleProject) {
        val checkGitStatus: TaskProvider<CheckGitTask> =
            project.tasks.register("checkGitStatus", CheckGitTask::class.java, project)
        val release: TaskProvider<Task> = project.tasks.register("release") {
            it.group = "publishing"
            it.description = "Releases a new version to Maven, Github, Curseforge and Modrinth"
            it.dependsOn(checkGitStatus)
        }

        fun configureReleaseSubtask(name: String) {
            try {
                val subtask = project.tasks.named(name) { it.mustRunAfter(checkGitStatus) }
                release.configure { it.dependsOn(subtask) }
            } catch (_: UnknownTaskException) {
                release.configure {
                    it.doFirst { project.logger.warn("Task $name not found; skipping it for release") }
                }
            }
        }

        configureReleaseSubtask("artifactoryPublish")
        configureReleaseSubtask("curseforge")
        configureReleaseSubtask("githubRelease")
        configureReleaseSubtask("modrinth")
    }

    private fun setupRepositoryExtensions(project: Project) {
        val repoExts: Map<String, Function<RepositoryHandler, Unit>> = Arrays.stream(
            Class.forName("io.github.ladysnake.chenille.api.ChenilleRepoExtensions").declaredMethods
        ).filter {
            Modifier.isPublic(it.modifiers) && it.name != "getChenille"
        }.collect(Collectors.toMap({ it.name }, { m -> Function<RepositoryHandler, Unit> { h -> m.invoke(null, h)}}))

        RepositoryHandlerChenilleExtensionImpl.instance = RepositoryHandlerChenilleExtensionImpl(project.repositories, repoExts.values)

        Class.forName("io.github.ladysnake.chenille.ChenilleGroovifier")
            .getMethod("setupRepositoryExtensions", Project::class.java, Map::class.java, RepositoryHandlerChenilleExtension::class.java)(null, project, repoExts, RepositoryHandlerChenilleExtensionImpl.instance)
    }

    private fun setupConfigurations(configurations: ConfigurationContainer) {
        configurations.register("modBundledImplementation") {
            configurations.getByName("modImplementation").extendsFrom(it)
            configurations.getByName("include").extendsFrom(it)
        }
        configurations.register("modBundledApi") {
            configurations.getByName("modApi").extendsFrom(it)
            configurations.getByName("include").extendsFrom(it)
        }
        configurations.register("modOptionalImplementation") {
            configurations.getByName("modCompileOnly").extendsFrom(it)
            configurations.getByName("modLocalRuntime").extendsFrom(it)
        }
    }
}
