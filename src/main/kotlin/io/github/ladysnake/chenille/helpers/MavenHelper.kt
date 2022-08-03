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
package io.github.ladysnake.chenille.helpers

import io.github.ladysnake.chenille.ChenilleProject
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication

internal object MavenHelper {
    fun configureDefaults(project: ChenilleProject) {
        project.pluginManager.apply("maven-publish")

        project.extensions.configure(PublishingExtension::class.java) { ext ->
            ext.publications { pubs ->
                if (pubs.findByName("mavenJava") == null) {
                    pubs.create("mavenJava", MavenPublication::class.java) { pub ->
                        pub.from(project.components.getByName("java"))
                    }
                }
            }
        }
    }
}
