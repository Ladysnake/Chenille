package io.github.ladysnake.chenille

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.ConfigurationContainer

@Suppress("unused") // Plugin entrypoint duh
class ChenilleGradlePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.plugins.apply("org.cadixdev.licenser")
        target.plugins.apply("fabric-loom")
        target.plugins.apply("com.matthewprenger.cursegradle")

        val project = ChenilleProject(target)

        LicenserHelper(project).configureDefaults()
        CurseGradleHelper(project).configureDefaults()
        setupConfigurations(project.configurations)
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
