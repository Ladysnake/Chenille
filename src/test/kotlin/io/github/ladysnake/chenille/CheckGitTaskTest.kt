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

import io.mockk.every
import io.mockk.mockk
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class CheckGitTaskTest {
    @ParameterizedTest
    @ValueSource(strings = ["1.21", "1.21.1", "1.21.1-fabric", "1.20-quilt"])
    fun `Regex allows valid version branches`(branchName: String) {
        Assertions.assertTrue(CheckGitTask.versionBranchRegex.matches(branchName))
    }

    @Disabled("Groovy module issues I am too lazy to fix")
    @ParameterizedTest
    @ValueSource(strings = ["1.21", "1.21.1", "1.21.1-fabric", "1.20-quilt"])
    fun `nominal case`(branchName: String) {
        val project = mockk<ChenilleProject>()
        every { project.git } returns mockk {
            every { status() } returns mockk {
                every { isClean } returns true
            }
            every { currentBranch() } returns branchName
            every { fetch() } returns mockk()
            every { listTags() } returns emptyList()
            every { trackingStatus() } returns mockk {
                every { aheadCount } returns 0
                every { behindCount } returns 0
            }
        }
        val proj = ProjectBuilder.builder().build()
        val task = proj.tasks.create("checkGit", CheckGitTask::class.java)
        task.run()
    }
}
