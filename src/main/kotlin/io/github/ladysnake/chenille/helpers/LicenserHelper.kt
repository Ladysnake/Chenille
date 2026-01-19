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

import dev.yumi.gradle.licenser.YumiLicenserGradleExtension
import dev.yumi.gradle.licenser.api.rule.HeaderRule
import io.github.ladysnake.chenille.ChenilleProject
import java.util.*

internal object LicenserHelper {
    fun configure(project: ChenilleProject, license: String?, customLicense: Any?) {
        check(license == null || customLicense == null) {
            "You cannot set both license and customLicense at the same time"
        }

        project.pluginManager.apply("dev.yumi.gradle.licenser")

        if ((license != null) xor (customLicense != null)) {
            project.afterEvaluate { // afterEvaluate to account for latest displayName and owners
                project.extensions.configure(YumiLicenserGradleExtension::class.java) {
                    val (licenseName, licenseText) = if (license != null) {
                        license to project.extension.licenseHeader(license).get()
                    } else {
                        val file = project.file(customLicense!!) // either license is nonnull, or customLicense is
                        file.path to project.resources.text.fromFile(file)
                    }
                    project.logger.info("Configuring license header {}", licenseName)
                    val currentYear = Calendar.getInstance().get(Calendar.YEAR)
                    val firstYear = project.git?.firstCommit?.let { firstCommit ->
                        Calendar.getInstance().apply { timeInMillis = firstCommit.commitTime.toLong() * 1000 }
                            .get(Calendar.YEAR)
                    }
                    val year =
                        if (firstYear == null || currentYear == firstYear) "$currentYear" else "$firstYear-$currentYear"
                    val renderedText = licenseText.asString().replace("\\$\\{(.*?)}".toRegex()) { match ->
                        when (val g = match.groups[1]!!.value) {
                            "year" -> year
                            "projectDisplayName" -> project.extension.displayName
                            "projectOwners" -> project.extension.owners
                            "gplVersion" -> project.properties["gpl_version"]?.toString() ?: "3"
                            else -> g
                        }
                    }.split("\r?\n".toRegex())
                    project.logger.debug("License header text: {}", renderedText)
                    it.rule(HeaderRule.parse(licenseName, renderedText))
                }
            }
        }
    }
}
