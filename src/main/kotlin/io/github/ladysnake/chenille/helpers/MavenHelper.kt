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
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import java.net.URI

internal object MavenHelper {
    fun configureDefaults(project: ChenilleProject, ladysnakeMaven: LadysnakeMaven?) {
        project.pluginManager.apply("maven-publish")

        project.extensions.configure(PublishingExtension::class.java) { ext ->
            ext.publications { pubs ->
                if (pubs.findByName("mavenJava") == null) {
                    pubs.create("mavenJava", MavenPublication::class.java) { pub ->
                        pub.from(project.components.getByName("java"))
                    }
                }
            }
            ext.repositories { repos ->
                if (ladysnakeMaven != null) {
                    val ladysnakeMavenUsername = project.findProperty("ladysnake_maven_username")
                    val ladysnakeMavenPassword = project.findProperty("ladysnake_maven_password")
                    if (ladysnakeMavenUsername is String && ladysnakeMavenPassword is String) {
                        repos.maven { repo ->
                            repo.name = ladysnakeMaven.mavenName
                            repo.url = URI("https://maven.ladysnake.org/${ladysnakeMaven.path}/")
                            repo.credentials {
                                it.username = ladysnakeMavenUsername
                                it.password = ladysnakeMavenPassword
                            }
                        }
                    } else {
                        println("Cannot configure artifactory; please define ext.artifactoryUsername and ext.artifactoryPassword before running publish")
                    }
                }
            }
        }
    }

    enum class LadysnakeMaven(val mavenName: String, val path: String) {
        RELEASES("ladysnake-releases", "releases"), SNAPSHOTS("ladysnake-snapshots", "snapshots")
    }
}
