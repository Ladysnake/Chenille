/*
 * Chenille
 * Copyright (C) 2022-2024 Ladysnake
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
package io.github.ladysnake.chenille.api

import org.gradle.api.Action
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.Provider
import org.gradle.api.resources.TextResource
import java.io.File
import java.net.URL

interface ChenilleGradleExtension {
    val repositories: ChenilleRepositoryHandler
    fun repositories(action: Action<ChenilleRepositoryHandler>)
    fun repositories(action: ChenilleRepositoryHandler.() -> Unit)
    fun configurePublishing(action: Action<PublishingConfiguration>)
    fun configureTestmod()
    fun configureTestmod(action: Action<TestmodConfiguration>)
    fun licenseHeader(license: String): Provider<TextResource>
    var changelogFile: File
    var javaVersion: Int
    var modVersion: String
    var displayName: String

    /**
     * Set the license header to a predefined one
     */
    var license: String?

    /**
     * Set the license header to the contents of a file.
     *
     * Can be anything accepted in {@link org.gradle.api.Project#file(Object)}
     */
    var customLicense: Any?
    var owners: String
    var github: URL?
    var changelogUrl: URL?
}
