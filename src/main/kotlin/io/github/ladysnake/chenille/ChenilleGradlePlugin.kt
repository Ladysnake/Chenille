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

import io.github.ladysnake.chenille.api.ChenilleGradleExtension
import net.fabricmc.loom.util.Constants
import net.fabricmc.loom.util.Constants.Configurations.INCLUDE
import net.fabricmc.loom.util.Constants.Configurations.LOCAL_RUNTIME
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.ConfigurationContainer
import org.gradle.api.artifacts.UnknownConfigurationException
import org.gradle.api.plugins.PluginManager

@Suppress("unused") // Plugin entrypoint duh
class ChenilleGradlePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        if (!target.pluginManager.hasLoom() && !target.pluginManager.hasPlugin("org.quiltmc.loom")) {
            target.logger.error("No Loom plugin detected! You must apply quilt-loom or fabric-loom before chenille")
        }

        val project = ChenilleProject(target)

        project.extensions.create(ChenilleGradleExtension::class.java, "chenille", ChenilleGradleExtensionImpl::class.java, project)

        if (project.pluginManager.hasNewLoom()) {
            setupConfigurations(project.configurations)
        } else {
            setupRemappingConfigurations(project.configurations)
        }
    }

    private fun PluginManager.hasLoom() = hasNewLoom() || hasPlugin("fabric-loom")
    private fun PluginManager.hasNewLoom() = hasPlugin("net.fabricmc.fabric-loom")

    private fun setupRemappingConfigurations(configurations: ConfigurationContainer) {
        configurations.register("modIncludeImplementation") {
            configurations.getByName("modImplementation").extendsFrom(it)
            configurations.getByName(INCLUDE).extendsFrom(it)
        }
        configurations.register("modIncludeApi") {
            configurations.getByName("modApi").extendsFrom(it)
            configurations.getByName(INCLUDE).extendsFrom(it)
        }
        configurations.register("modLocalImplementation") {
            configurations.getByName("modCompileOnly").extendsFrom(it)
            configurations.getByName("modLocalRuntime").extendsFrom(it)
        }
    }

    private fun setupConfigurations(configurations: ConfigurationContainer) {
        configurations.register("includeImplementation") {
            configurations.getByName("implementation").extendsFrom(it)
            configurations.getByName(INCLUDE).extendsFrom(it)
        }
        configurations.register("includeApi") {
            configurations.getByName("api").extendsFrom(it)
            configurations.getByName(INCLUDE).extendsFrom(it)
        }
        configurations.register("localImplementation") {
            configurations.getByName("compileOnly").extendsFrom(it)
            configurations.getByName(LOCAL_RUNTIME).extendsFrom(it)
        }
    }
}
