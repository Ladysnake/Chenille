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

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import javax.inject.Inject

abstract class CheckGitTask @Inject constructor(private val project: ChenilleProject): DefaultTask() {
    companion object {
        val versionBranchRegex = """\d+\.\d+(?>\.\d+)?(?>-[\w-]+)?""".toRegex()
    }

    init {
        group = "publishing"
        description = "Checks that the git repository is in a state suitable for release"
    }

    @TaskAction
    fun run() {
        val git = project.git ?: throw IllegalStateException("No git repository")
        if (!git.status().isClean) {
            throw IllegalStateException("Git repository not ready for release (${git.status()})")
        }
        val currentBranch = git.currentBranch()
        if (currentBranch == null || (currentBranch != "main" && !currentBranch.matches(versionBranchRegex))) {
            throw IllegalStateException("Need to be on main or a version branch to release (currently on ${currentBranch})")
        }
        git.fetch()
        if (git.listTags().any { ref -> ref.shortName == project.version }) {
            throw IllegalStateException("A tag already exists for ${project.version}")
        }
        val status = git.trackingStatus() ?: throw IllegalStateException("No remote tracking branch set for $currentBranch")
        if (status.aheadCount != 0) {
            throw IllegalStateException("Some commits have not been pushed")
        }
        if (status.behindCount != 0) {
            throw IllegalStateException("Some commits have not been pulled")
        }
    }
}
