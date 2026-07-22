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
package io.github.ladysnake.chenille.helpers

import com.modrinth.minotaur.ModrinthExtension
import com.modrinth.minotaur.dependencies.DependencyType
import com.modrinth.minotaur.dependencies.ModDependency
import io.github.ladysnake.chenille.ChenilleProject
import io.github.ladysnake.chenille.api.PublishingConfiguration
import org.gradle.api.Project.DEFAULT_VERSION

internal object ModrinthHelper {
    fun configureDefaults(project: ChenilleProject, cfg: PublishingConfiguration) {
        project.pluginManager.apply("com.modrinth.minotaur")

        project.extensions.configure(ModrinthExtension::class.java) {
            val modrinthApiKey = project.providers.gradleProperty("modrinth_api_key")
            if (modrinthApiKey.isPresent) {
                token.set(modrinthApiKey)
            } else {
                project.logger.warn("Modrinth API Key not configured; please define the 'modrinth_api_key' user property before release")
                return@configure
            }
            if (project.hasProperty("modrinth_id")) {
                projectId.set(project.findProperty("modrinth_id")!!.toString())
                if (project.version == DEFAULT_VERSION) {
                    error("Project version has not been set - is the chenille configuration specified too early?")
                }
                versionNumber.set(project.version.toString())
                uploadFile.set(cfg.mainArtifact)
                changelog.set(project.providers.provider(project.changelog).map { it.toString() })
                versionType.set(project.findProperty("release_type")?.toString() ?: error(
                    "Please specify the release type using the 'release_type' project property"
                ))
                val mcVersions = (
                    project.findProperty("modrinth_versions") ?:
                    project.findProperty("curseforge_versions") ?:
                    project.findProperty("minecraft_version"))
                if (mcVersions == null) {
                    error("Missing one of the properties 'modrinth_versions', 'curseforge_versions', or 'minecraft_version'")
                }
                mcVersions.toString().split("; ").forEach {
                    gameVersions.add(it)
                }
                if (cfg.fabricCompatible) {
                    loaders.add("fabric")
                }
                if (cfg.quiltCompatible) {
                    loaders.add("quilt")
                }
                if (cfg.neoforgeCompatible) {
                    loaders.add("neoforge")
                }

                fun applyRelations(key: String, type: DependencyType) {
                    if (project.hasProperty(key)) {
                        project.property(key).toString().split(Regex(";\\s*")).forEach { slug ->
                            dependencies.add(ModDependency(slug.trim(), type))
                        }
                    }
                }

                applyRelations("mr_requirements", DependencyType.REQUIRED)
                applyRelations("mr_optionals", DependencyType.OPTIONAL)
                applyRelations("mr_embeddeds", DependencyType.EMBEDDED)
                applyRelations("mr_incompatibles", DependencyType.INCOMPATIBLE)
            } else {
                project.logger.lifecycle("Modrinth Project ID not configured; please define the 'modrinth_id' project property before release")
            }
        }
    }
}
