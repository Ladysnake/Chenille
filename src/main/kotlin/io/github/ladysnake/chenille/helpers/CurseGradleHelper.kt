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

import com.matthewprenger.cursegradle.CurseArtifact
import com.matthewprenger.cursegradle.CurseExtension
import com.matthewprenger.cursegradle.CurseProject
import com.matthewprenger.cursegradle.CurseRelation
import groovy.lang.Closure
import io.github.ladysnake.chenille.ChenilleProject
import org.gradle.api.tasks.bundling.AbstractArchiveTask

internal object CurseGradleHelper {
    fun configureDefaults(project: ChenilleProject, mainArtifact: Any) {
        project.plugins.apply("com.matthewprenger.cursegradle")

        fun CurseRelation.applyRelation(key: String, action: CurseRelation.(String) -> Unit) {
            if (project.hasProperty(key)) {
                project.properties[key].toString().split(";").forEach { slug ->
                    action(slug.trim())
                }
            }
        }

        project.extensions.configure(CurseExtension::class.java) { ext ->
            ext.apiKey = project.findProperty("curseforge_api_key") ?: "".also {
                println("Curseforge API Key not configured; please define the 'curseforge_api_key' user property before release")
            }

            if (project.hasProperty("curseforge_id")) {
                ext.project { proj: CurseProject ->
                    proj.id = project.findProperty("curseforge_id")

                    proj.releaseType = project.properties["release_type"] ?: throw IllegalStateException(
                        "Please specify the release type using the 'release_type' project property"
                    )

                    //usually automatically determined by the CurseGradle plugin, but won't work with fabric
                    val curseforgeVersions = project.properties["curseforge_versions"] ?: throw IllegalStateException(
                        "Please specify the compatible minecraft versions using the 'curseforge_versions' project property"
                    )
                    curseforgeVersions.toString().split("; ").forEach(proj::addGameVersion)
                    proj.addGameVersion("Fabric")

                    proj.mainArtifact(project.file((mainArtifact as? AbstractArchiveTask)?.archiveFile ?: mainArtifact)) { artifact: CurseArtifact ->
                        artifact.displayName = "${project.name}-${project.version}.jar"

                        if (
                            project.hasProperty("cf_requirements") ||
                            project.hasProperty("cf_optionals") ||
                            project.hasProperty("cf_embeddeds") ||
                            project.hasProperty("cf_tools") ||
                            project.hasProperty("cf_incompatibles") ||
                            project.hasProperty("cf_includes")
                        ) {
                            artifact.relations { relations: CurseRelation ->
                                relations.applyRelation("cf_requirements") { requiredDependency(it) }
                                relations.applyRelation("cf_optionals") { optionalDependency(it) }
                                relations.applyRelation("cf_embeddeds") { embeddedLibrary(it) }
                                relations.applyRelation("cf_tools") { tool(it) }
                                relations.applyRelation("cf_incompatibles") { incompatible(it) }
                            }
                        }
                    }

                    proj.changelogType = "markdown"
                    proj.changelog = project.changelog
                }
            } else {
                println("Curseforge Project ID not configured; please define the 'curseforge_id' project property before release")
            }
        }
    }

    private fun CurseExtension.project(action: (CurseProject) -> Unit) = project(object : Closure<Unit>(this, this) {
        @Suppress("unused") // to be called dynamically by Groovy
        fun doCall(proj: CurseProject) = action(proj)
    })

    private fun CurseProject.mainArtifact(artifact: Any, action: (CurseArtifact) -> Unit) = mainArtifact(artifact, object: Closure<Unit>(this, this) {
        @Suppress("unused") // to be called dynamically by Groovy
        fun doCall(artif: CurseArtifact) = action(artif)
    })

    private fun CurseArtifact.relations(action: (CurseRelation) -> Unit) = relations(object: Closure<Unit>(this, this) {
        @Suppress("unused") // to be called dynamically by Groovy
        fun doCall(rel: CurseRelation) = action(rel)
    })
}
