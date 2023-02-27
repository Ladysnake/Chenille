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
package io.github.ladysnake.chenille.helpers

import io.github.ladysnake.chenille.ChenilleProject
import org.jfrog.gradle.plugin.artifactory.dsl.ArtifactoryPluginConvention

internal object ArtifactoryHelper {
    fun configureDefaults(project: ChenilleProject) {
        project.pluginManager.apply("com.jfrog.artifactory")

        if (project.hasProperty("artifactory_user")) {
            @Suppress("DEPRECATION")    // Artifactory bad >:(
            project.convention.getPlugin(ArtifactoryPluginConvention::class.java).let {
                if (project.isLadysnakeProject()) it.setContextUrl("https://ladysnake.jfrog.io/artifactory/")
                it.publish { cfg ->
                    cfg.repository { repo ->
                        if (project.isLadysnakeProject()) repo.setRepoKey("mods")

                        repo.setUsername(project.findProperty("artifactory_user"))
                        repo.setPassword(project.findProperty("artifactory_api_key"))
                    }
                    cfg.defaults { def ->
                        def.publications("mavenJava")

                        def.setPublishArtifacts(true)
                        def.setPublishPom(true)
                    }
                }
            }
        } else {
            println("Artifactory not configured; please define the 'artifactory_user' and 'artifactory_api_key' properties before running artifactoryPublish")
        }
        project.tasks.findByName("artifactoryPublish")?.dependsOn("build")
    }
}
