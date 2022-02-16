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
package io.github.ladysnake.chenille.helpers

import com.modrinth.minotaur.TaskModrinthUpload
import io.github.ladysnake.chenille.ChenilleProject

class ModrinthHelper(private val project: ChenilleProject) {
    fun configureDefaults() {
        project.tasks.register("modrinth", TaskModrinthUpload::class.java) { task ->
            if (project.hasProperty("modrinth_key")) {
                task.token = project.findProperty("modrinth_key")!!.toString()
            } else {
                println("Modrinth API Key not configured; please define the 'modrinth_key' user property before release")
                return@register
            }
            if (project.hasProperty("modrinth_id")) {
                task.projectId = project.findProperty("modrinth_id")!!.toString()
                task.versionNumber = project.version.toString()
                task.uploadFile = project.tasks.findByName("remapJar")
                project.subprojects { subproject ->
                    task.addFile(subproject.tasks.getByName("remapJar"))
                }
                task.changelog = project.changelog.toString()
                "${project.findProperty("curseforge_versions")}".split("; ").forEach {
                    task.addGameVersion(it)
                }
                task.addLoader("fabric")
            } else {
                println("Modrinth Project ID not configured; please define the 'modrinth_id' project property before release")
            }
        }
    }
}
