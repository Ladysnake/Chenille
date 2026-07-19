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
package io.github.ladysnake.chenille.api

import io.github.ladysnake.chenille.ChenilleProject
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider

abstract class CurseforgeGradleExtension(val project: ChenilleProject) {

    companion object {
        const val EXTENSION_NAME = "curseforge"
    }

    abstract val apiKey: Property<String>
    abstract val projectId: Property<String>

    abstract val releaseType: Property<String>
    abstract val displayName: Property<String>

    abstract val gameVersions: ListProperty<String>
    abstract val supportedEnvironments: ListProperty<String>

    abstract val changelogType: Property<String>
    abstract val changelogText: Property<String>

    // relations
    abstract val relationsRequired: ListProperty<String>
    abstract val relationsOptional: ListProperty<String>
    abstract val relationsEmbedded: ListProperty<String>
    abstract val relationsTool: ListProperty<String>
    abstract val relationsIncompatible: ListProperty<String>

    init {
        apiKey.convention(project.providers.gradleProperty("curseforge_api_key"))
        projectId.convention(project.provider { project.findProperty("curseforge_id")?.toString() })

        releaseType.convention(project.provider { project.findProperty("release_type")?.toString() })
        displayName.convention(project.provider { project.findProperty("curseforge_display_name")?.toString() }.orElse(project.provider { "${project.name}-${project.version}.jar" }))

        gameVersions.convention(project.provider { project.findProperty("curseforge_versions")?.toString() }.split())
        supportedEnvironments.convention(project.provider { project.findProperty("curseforge_environments")?.toString() }.split())

        changelogType.convention("markdown")
        changelogText.convention(project.provider { project.changelog.call().toString() })

        relationsRequired.convention(project.provider { project.findProperty("cf_requirements")?.toString() }.split())
        relationsOptional.convention(project.provider { project.findProperty("cf_optionals")?.toString() }.split())
        relationsEmbedded.convention(project.provider { project.findProperty("cf_embeddeds")?.toString() }.split())
        relationsTool.convention(project.provider { project.findProperty("cf_tools")?.toString() }.split())
        relationsIncompatible.convention(project.provider { project.findProperty("cf_incompatibles")?.toString() }.split())
    }

    private fun Provider<String>.split(): Provider<List<String>> = this.map { it.split(Regex(";\\s*")) }
}
