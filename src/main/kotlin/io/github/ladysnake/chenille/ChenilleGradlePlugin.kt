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
import io.github.ladysnake.chenille.api.CurseforgeGradleExtensionImpl
import net.fabricmc.loom.util.Constants.Configurations.INCLUDE
import net.fabricmc.loom.util.Constants.Configurations.LOCAL_RUNTIME
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.ConfigurationContainer
import org.gradle.api.plugins.PluginManager

@Suppress("unused") // Plugin entrypoint duh
class ChenilleGradlePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        if (!target.pluginManager.hasLoom()) {
            target.logger.error("No Loom plugin detected! You must apply net.fabricmc.fabric-loom (or a known variant of it) before chenille")
        }

        val project = ChenilleProject(target)

        project.extensions.create(ChenilleGradleExtension::class.java, "chenille", ChenilleGradleExtensionImpl::class.java, project)
        project.extensions.create(CurseforgeGradleExtensionImpl.EXTENSION_NAME, CurseforgeGradleExtensionImpl::class.java, project)

        if (project.usesRemapLoom()) {
            setupRemappingConfigurations(project.configurations)
        } else {
            setupConfigurations(project.configurations)
        }
    }

    private fun PluginManager.hasLoom() = hasFabricLoom() || hasQuiltLoom() || hasNeoLoom()
    private fun PluginManager.hasFabricLoom() = hasPlugin("net.fabricmc.fabric-loom") || hasPlugin("net.fabricmc.fabric-loom-remap")
    private fun PluginManager.hasQuiltLoom() = hasPlugin("org.quiltmc.loom")
    private fun PluginManager.hasNeoLoom() = hasPlugin("org.relativitymc.neo-loom")

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
