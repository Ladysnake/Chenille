/*
 * Chenille
 * Copyright (C) 2022-2025 Ladysnake
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

import io.github.ladysnake.chenille.ChenilleProject
import org.cadixdev.gradle.licenser.LicenseExtension
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.Provider
import java.util.*

internal object LicenserHelper {
    fun configure(project: ChenilleProject, license: String?, customLicense: Any?) {
        check(license == null || customLicense == null) {
            "You cannot set both license and customLicense at the same time"
        }

        project.pluginManager.apply("org.cadixdev.licenser")

        if (license != null || customLicense != null) {
            project.extensions.configure(LicenseExtension::class.java) {
                if (license != null) {
                    it.header.set(project.extension.licenseHeader(license))
                } else {
                    it.header(customLicense)
                }
                it.newLine.set(false)
                it.properties { ext ->
                    val currentYear = Calendar.getInstance().get(Calendar.YEAR)
                    val firstYear = project.git?.firstCommit?.let { firstCommit ->
                        Calendar.getInstance().apply { timeInMillis = firstCommit.commitTime.toLong() * 1000 }
                            .get(Calendar.YEAR)
                    }
                    val year =
                        if (firstYear == null || currentYear == firstYear) currentYear else "$firstYear-$currentYear"
                    ext["year"] = year
                    ext["projectDisplayName"] = project.extension.displayName
                    ext["projectOwners"] = project.extension.owners
                    if (license?.contains("GPL") == true) ext["gplVersion"] = project.properties["gpl_version"] ?: "3"
                }
            }
        }
    }
}
