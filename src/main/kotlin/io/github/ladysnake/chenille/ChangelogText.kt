package io.github.ladysnake.chenille

import java.io.IOException
import java.lang.IllegalStateException
import java.nio.file.Path
import kotlin.io.path.absolute
import kotlin.io.path.createDirectories
import kotlin.io.path.createFile
import kotlin.io.path.exists
import kotlin.io.path.useLines
import kotlin.io.path.writeText

class ChangelogText(private val changelogFile: Path, currentVersion: String, changelogBaseUrl: String) {
    companion object {
        private const val separator = "---"
    }

    private val text by lazy {
        if (!changelogFile.exists()) {
            println("No changelog file found, creating one at \"${changelogFile.absolute()}\"")
            try {
                changelogFile.parent.createDirectories()
                changelogFile.createFile()
                changelogFile.writeText(
                    """
                        ------------------------------------------------------
                        Version $currentVersion
                        ------------------------------------------------------
                        Additions
                        - None

                        Changes
                        - None

                        Bug Fixes
                        - None

                        """.trimIndent()
                )

                return@lazy ""
            } catch (e: IOException) {
                println("Unable to write changelog file: " + e.message)
                e.printStackTrace()
                return@lazy ""
            }
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
            }
        }.takeWhile { line -> !line.startsWith(separator) }

        return@lazy """
            ${changelog.joinToString("\n")}


             see full changelog [here]($changelogBaseUrl/$currentVersion/changelog.md "Changelog")
            """.trimIndent()
    }

    override fun toString(): String {
        return text
    }
}
