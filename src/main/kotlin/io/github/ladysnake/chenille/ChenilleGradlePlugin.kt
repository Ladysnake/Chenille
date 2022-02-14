package io.github.ladysnake.chenille

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.UnknownTaskException
import org.gradle.api.artifacts.ConfigurationContainer
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.tasks.TaskProvider

@Suppress("unused") // Plugin entrypoint duh
class ChenilleGradlePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.plugins.apply("org.cadixdev.licenser")
        target.plugins.apply("fabric-loom")
        target.plugins.apply("com.matthewprenger.cursegradle")

        val project = ChenilleProject(target)

        configureReleaseTask(project)

        LicenserHelper(project).configureDefaults()
        CurseGradleHelper(project).configureDefaults()

        setupConfigurations(project.configurations)
        setupRepositoryExtensions(project)
    }

    private fun configureReleaseTask(project: ChenilleProject) {
        val checkGitStatus: TaskProvider<CheckGitTask> =
            project.tasks.register("checkGitStatus", CheckGitTask::class.java, project)
        val release = project.tasks.register("release") {
            it.group = "publishing"
            it.description = "Releases a new version to Maven, Github, Curseforge and Modrinth"
            it.dependsOn(checkGitStatus)
        }

        fun configureReleaseSubtask(name: String) {
            try {
                project.tasks.named(name) { subtask ->
                    subtask.mustRunAfter(checkGitStatus)
                    release.configure { it.dependsOn(subtask) }
                }
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

    private fun setupRepositoryExtensions(project: ChenilleProject) {
        val repoExts = mapOf<String, RepositoryHandler.() -> Unit>(
            "cursemaven" to RepositoryHandler::cursemaven,
            "jitpack" to RepositoryHandler::jitpack,
            "ladysnake" to RepositoryHandler::ladysnake,
            "lucko" to RepositoryHandler::lucko,
            "terraformers" to RepositoryHandler::terraformers
        )

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
