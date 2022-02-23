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

import com.github.breadmoirai.githubreleaseplugin.GithubReleaseExtension
import io.github.ladysnake.chenille.ChenilleProject
import net.fabricmc.loom.task.RemapJarTask

class GithubReleaseHelper(private val project: ChenilleProject) {
    fun configureDefaults() {
        project.extensions.configure(GithubReleaseExtension::class.java) {
            it.token("${project.findProperty("github_releases_token")}")
            // default owner: last component of maven group
            // default repo: name of the project
            it.setTagName(project.version.toString())
            project.git?.run { it.setTargetCommitish { currentBranch() } }
            it.setBody(project.changelog)

            it.setReleaseAssets(project.tasks.named("remapJar", RemapJarTask::class.java))
        }
    }
}