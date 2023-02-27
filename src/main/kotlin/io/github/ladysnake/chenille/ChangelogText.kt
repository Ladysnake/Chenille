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

import org.slf4j.LoggerFactory
import java.io.IOException
import java.util.concurrent.Callable
import kotlin.io.path.absolute
import kotlin.io.path.createDirectories
import kotlin.io.path.createFile
import kotlin.io.path.exists
import kotlin.io.path.useLines
import kotlin.io.path.writeText

class ChangelogText(project: ChenilleProject): Callable<CharSequence> {
    companion object {
        private const val separator = "---"
        private val logger = LoggerFactory.getLogger(ChangelogText::class.java)
    }

    private val text by lazy {
        logger.info("Resolving changelog")

        val changelogFile = project.extension.changelogFile.toPath()

        if (!changelogFile.exists()) {
            println("No changelog file found, creating one at \"${changelogFile.absolute()}\"")
            try {
                changelogFile.parent.createDirectories()
                changelogFile.createFile()
                changelogFile.writeText(
                    """
                        |------------------------------------------------------
                        |Version ${project.extension.modVersion}
                        |------------------------------------------------------
                        |Additions
                        |- None
                        |
                        |Changes
                        |- None
                        |
                        |Bug Fixes
                        |- None
                        |
                    """.trimMargin()
                )
            } catch (e: IOException) {
                println("Unable to write changelog file: " + e.message)
                e.printStackTrace()
            }
            return@lazy ""
        }
        val changelog = changelogFile.useLines { lines ->
            sequence {
                val iterator = lines.iterator()
                if (!iterator.hasNext() || !iterator.next()
                        .startsWith(separator)
                ) throw IllegalStateException("Malformed changelog: expected separator \"${separator}\" on line 1")
                if (!iterator.hasNext()) throw IllegalStateException("Malformed changelog: expected version name on line 2")
                yield("${iterator.next()}:\n")
                if (!iterator.hasNext() || !iterator.next()
                        .startsWith(separator)
                ) throw IllegalStateException("Malformed changelog: expected separator \"${separator}\" on line 3")
                if (!iterator.hasNext()) throw IllegalStateException("Malformed changelog: expected description starting at line 4")
                yieldAll(iterator)
            }.takeWhile { line ->
                !line.startsWith(separator)
            }.joinToString("\n|")
        }

        """
        |$changelog
        |
        |
        | see full changelog [here](${project.extension.changelogUrl ?: return@lazy changelog} "Changelog")
        """.trimMargin()
    }

    override fun toString(): String {
        return text
    }

    override fun call(): CharSequence = this.text
}
