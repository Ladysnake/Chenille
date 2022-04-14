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
package io.github.ladysnake.chenille.api

interface PublishingConfiguration {
    /**
     * The upload artifact file. This can be any object type that is resolvable by
     * [org.gradle.api.Project.file].
     */
    var mainArtifact: Any

    /**
     * Publishes maven artifacts to artifactory. Uses the project's publishing configuration.
     */
    fun withArtifactory()

    /**
     * Publishes the [mainArtifact] and sources through a Github release.
     */
    fun withGithubRelease()

    /**
     * Publishes the [mainArtifact] through a curseforge release.
     */
    fun withCurseforgeRelease()

    /**
     * Publishes the [mainArtifact] through a modrinth release.
     */
    fun withModrinthRelease()
}
