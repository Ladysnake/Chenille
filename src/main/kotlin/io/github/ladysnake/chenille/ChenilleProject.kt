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
package io.github.ladysnake.chenille

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.errors.RepositoryNotFoundException
import org.gradle.api.Project

class ChenilleProject(private val project: Project): Project by project {
    val git: JGitWrapper? by lazy {
        try { JGitWrapper(Git.open(rootDir)) } catch (e: RepositoryNotFoundException) { null }
    }
    val changelog = ChangelogText(
        project.file("changelog.md").toPath(),
        currentVersion = project.properties["mod_version"].toString(),
        changelogBaseUrl = project.properties["changelog_base_url"].toString()
    )
    fun isLadysnakeProject() = project.group.toString().takeIf { it.contains("ladysnake") || it.contains("onyxstudios") } != null
}
