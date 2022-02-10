package io.github.ladysnake.chenille

import org.cadixdev.gradle.licenser.LicenseExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.ConfigurationContainer
import java.io.File
import java.util.*

@Suppress("unused") // Plugin entrypoint duh
class ChenilleGradlePlugin : Plugin<Project> {
    private lateinit var rootDir: File
    private val git by lazy { JGitWrapper(rootDir) }

    override fun apply(target: Project) {
        rootDir = target.rootDir
        target.plugins.apply("org.cadixdev.licenser")
        target.plugins.apply("fabric-loom")
        configureLicenser(target)
        setupConfigurations(target.configurations)
    }

    private fun configureLicenser(target: Project) {
        val license = target.properties["license_header"]?.toString()?.uppercase()
        if (license != null) {
            target.extensions.configure(LicenseExtension::class.java) {
                it.header.set(target.provider {
                    target.resources.text.fromUri(
                        ChenilleGradlePlugin::class.java.getResource("/license_headers/${license}.txt")
                            ?: throw IllegalArgumentException("$license is not a recognized license header")
                    )
                })
                it.newLine.set(false)
                it.properties { ext ->
                    val currentYear = Calendar.getInstance().get(Calendar.YEAR)
                    val firstYear = git.firstCommit?.let { firstCommit ->
                        Calendar.getInstance().apply { timeInMillis = firstCommit.commitTime.toLong() }
                            .get(Calendar.YEAR) }
                    val year = if (firstYear == null || currentYear == firstYear) currentYear else "$firstYear-$currentYear"
                    ext["year"] = year
                    ext["projectDisplayName"] = target.properties["display_name"]
                    ext["projectOwners"] = target.properties["owners"]
                    if (license.contains("GPL")) ext["gplVersion"] = target.properties["gpl_version"] ?: "3"
                }
            }
        }
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
