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
package io.github.ladysnake.chenille.helpers

import com.modrinth.minotaur.ModrinthExtension
import io.github.ladysnake.chenille.ChenilleProject

internal object ModrinthHelper {
    fun configureDefaults(project: ChenilleProject, mainArtifact: Any) {
        project.pluginManager.apply("com.modrinth.minotaur")

        project.extensions.configure(ModrinthExtension::class.java) { ext ->
            if (project.hasProperty("modrinth_api_key")) {
                ext.token.set(project.findProperty("modrinth_api_key")!!.toString())
            } else {
                println("Modrinth API Key not configured; please define the 'modrinth_key' user property before release")
                return@configure
            }
            if (project.hasProperty("modrinth_id")) {
                ext.projectId.set(project.findProperty("modrinth_id")!!.toString())
                ext.versionNumber.set(project.version.toString())
                ext.uploadFile.set(mainArtifact)
                project.subprojects { subproject ->
                    ext.additionalFiles.add(subproject.tasks.getByName("remapJar"))
                }
                ext.changelog.set(project.providers.provider(project.changelog).map { it.toString() })
                "${project.findProperty("curseforge_versions")}".split("; ").forEach {
                    ext.gameVersions.add(it)
                }
                if (project.isFabricMod) {
                    ext.loaders.add("fabric")
                } else {
                    ext.loaders.add("quilt")
                }
            } else {
                println("Modrinth Project ID not configured; please define the 'modrinth_id' project property before release")
            }
        }
    }
}
