package io.github.ladysnake.chenille

import io.github.ladysnake.chenille.helpers.ArtifactoryHelper
import io.github.ladysnake.chenille.helpers.CurseGradleHelper
import io.github.ladysnake.chenille.helpers.GithubReleaseHelper
import io.github.ladysnake.chenille.helpers.LicenserHelper
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
            Class.forName("io.github.ladysnake.chenille.ChenilleRepoExtensions").declaredMethods
        ).filter {
            Modifier.isPublic(it.modifiers)
        }.collect(Collectors.toMap({ it.name }, { m -> Function<RepositoryHandler, Unit> { h -> m.invoke(null, h)}}))

        Class.forName("io.github.ladysnake.chenille.ChenilleGroovifier")
            .getMethod("setupRepositoryExtensions", Project::class.java, Map::class.java)(null, project, repoExts)
    }

    private fun setupConfigurations(configurations: ConfigurationContainer) {
        configurations.create("modBundledImplementation").apply {
            configurations.getByName("modImplementation").extendsFrom(this)
            configurations.getByName("include").extendsFrom(this)
        }
        configurations.create("modBundledApi").apply {
            configurations.getByName("modApi").extendsFrom(this)
            configurations.getByName("include").extendsFrom(this)
        }
        configurations.create("modOptionalImplementation").apply {
            configurations.getByName("modCompileOnly").extendsFrom(this)
            configurations.getByName("modLocalRuntime").extendsFrom(this)
        }
    }
}
