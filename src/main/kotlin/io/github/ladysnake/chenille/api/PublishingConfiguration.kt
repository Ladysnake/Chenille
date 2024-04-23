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
     * Publishes maven artifacts to the Ladysnake maven. Uses the project's publishing configuration.
     *
     * This method uses the `ladysnake_maven_username` and `ladysnake_maven_password` properties to set maven credentials.
     *
     * @param lifecycle determines whether to publish to the
     */
    @Suppress("OVERLOADS_INTERFACE")    // it's ok, we have the right compiler options
    @JvmOverloads   // haha groovy interoperability
    fun withLadysnakeMaven(lifecycle: ArtifactLifecycle = ArtifactLifecycle.AUTO)

    /**
     * Publishes maven artifacts to the Ladysnake maven. Uses the project's publishing configuration.
     *
     * This method uses the `ladysnake_maven_username` and `ladysnake_maven_password` properties to set maven credentials.
     *
     * @param snapshot if true, will publish to the Ladysnake `snapshots` repository instead of the default `releases`
     */
    fun withLadysnakeMaven(snapshot: Boolean) = withLadysnakeMaven(if (snapshot) ArtifactLifecycle.SNAPSHOT else ArtifactLifecycle.RELEASE)

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
