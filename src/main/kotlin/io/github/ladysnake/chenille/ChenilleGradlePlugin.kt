/*
 * Chenille
 * Copyright (C) 2022-2023 Ladysnake
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
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.ConfigurationContainer

@Suppress("unused") // Plugin entrypoint duh
class ChenilleGradlePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        if (!target.pluginManager.hasPlugin("fabric-loom")) {
            target.pluginManager.apply("org.quiltmc.loom")
        }

        val project = ChenilleProject(target)

        project.extensions.create(ChenilleGradleExtension::class.java, "chenille", ChenilleGradleExtensionImpl::class.java, project)

        setupConfigurations(project.configurations)
    }

    private fun setupConfigurations(configurations: ConfigurationContainer) {
        configurations.register("modIncludeImplementation") {
            configurations.getByName("modImplementation").extendsFrom(it)
            configurations.getByName("include").extendsFrom(it)
        }
        configurations.register("modIncludeApi") {
            configurations.getByName("modApi").extendsFrom(it)
            configurations.getByName("include").extendsFrom(it)
        }
        configurations.register("modLocalImplementation") {
            configurations.getByName("modCompileOnly").extendsFrom(it)
            configurations.getByName("modLocalRuntime").extendsFrom(it)
        }
    }
}
