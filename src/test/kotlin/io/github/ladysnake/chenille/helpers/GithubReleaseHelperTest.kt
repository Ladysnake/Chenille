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

import com.github.breadmoirai.githubreleaseplugin.GithubReleaseExtension
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class GithubReleaseHelperTest {
    @Test
    fun `extractRepositoryProperties parses github URL`() {
        val ext = mockk<GithubReleaseExtension>(relaxed = true)
        assertFalse(GithubReleaseHelper.extractRepositoryProperties(ext, "https://github.com/Ladysnake/Chenille", mockk(relaxed = true)))
        verify {
            ext.owner.set("Ladysnake")
            ext.repo.set("Chenille")
        }
    }

    @Test
    fun `extractRepositoryProperties fails on non-github URL`() {
        val ext = mockk<GithubReleaseExtension>(relaxed = true)
        assertTrue(GithubReleaseHelper.extractRepositoryProperties(ext, "https://gitlab.com/Ladysnake/Chenille", mockk(relaxed = true)))
        verify(inverse = true) {
            ext.owner.set("Ladysnake")
            ext.repo.set("Chenille")
        }
    }
}
