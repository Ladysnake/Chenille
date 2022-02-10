package io.github.ladysnake.chenille

import org.cadixdev.gradle.licenser.LicenseExtension
import java.util.*

internal class LicenserHelper(private val project: ChenilleProject) {
    fun configureDefaults() {
        val license = project.properties["license_header"]?.toString()?.uppercase()
        if (license != null) {
            project.extensions.configure(LicenseExtension::class.java) {
                it.header.set(project.provider {
                    project.resources.text.fromUri(
                        ChenilleGradlePlugin::class.java.getResource("/license_headers/${license}.txt")
                            ?: throw IllegalArgumentException("$license is not a recognized license header")
                    )
                })
                it.newLine.set(false)
                it.properties { ext ->
                    val currentYear = Calendar.getInstance().get(Calendar.YEAR)
                    val firstYear = project.git.firstCommit?.let { firstCommit ->
                        Calendar.getInstance().apply { timeInMillis = firstCommit.commitTime.toLong() }
                            .get(Calendar.YEAR)
                    }
                    val year =
                        if (firstYear == null || currentYear == firstYear) currentYear else "$firstYear-$currentYear"
                    ext["year"] = year
                    ext["projectDisplayName"] = project.properties["display_name"]
                    ext["projectOwners"] = project.properties["owners"]
                    if (license.contains("GPL")) ext["gplVersion"] = project.properties["gpl_version"] ?: "3"
                }
            }
        }
    }
}
