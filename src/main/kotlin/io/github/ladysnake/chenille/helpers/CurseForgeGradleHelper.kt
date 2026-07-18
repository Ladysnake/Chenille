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

import io.github.ladysnake.chenille.ChenilleProject
import io.github.ladysnake.chenille.api.CurseforgeGradleExtension
import io.github.ladysnake.chenille.api.PublishingConfiguration
import net.darkhax.curseforgegradle.TaskPublishCurseForge
import org.gradle.api.DefaultTask
import org.gradle.api.Task
import org.gradle.api.tasks.TaskProvider

internal object CurseForgeGradleHelper {
    fun configureDefaults(project: ChenilleProject, cfg: PublishingConfiguration): TaskProvider<out Task> {

        val mainTask = project.tasks.register("curseforge", DefaultTask::class.java) {
            description = "Main task for publishing to Curseforge"
        }

        project.pluginManager.withPlugin("net.darkhax.curseforgegradle") {
            val ext = project.extensions.getByType(CurseforgeGradleExtension::class.java)

            if(!ext.projectId.isPresent) {
                project.logger.warn("Curseforge Project ID not configured; please define the 'curseforge_id' project property before release")
                return@withPlugin
            }
            if(!ext.apiKey.isPresent) {
                project.logger.warn("Curseforge API Key not configured; please define the 'curseforge_api_key' user property before release")
                return@withPlugin
            }

            val publishTask = project.tasks.register("curseforge${ext.projectId.get()}", TaskPublishCurseForge::class.java) {
                inputs.file(cfg.mainArtifact)

                apiToken = ext.apiKey.get()
                val mainFile = upload(ext.projectId.get(), project.file(cfg.mainArtifact))

                mainFile.displayName = ext.displayName.get()

                mainFile.changelogType = ext.changelogType.get()
                mainFile.changelog = ext.changelogText.orNull

                mainFile.gameVersions.addAll(ext.gameVersions.get())

                if(ext.releaseType.isPresent) {
                    mainFile.releaseType = ext.releaseType.get()
                }

                // usually automatically determined by CurseForgeGradle
                if (cfg.fabricCompatible) {
                    mainFile.gameVersions.add("Fabric")
                }
                if (cfg.quiltCompatible) {
                    mainFile.gameVersions.add("Quilt")
                }
                if (cfg.neoforgeCompatible) {
                    mainFile.gameVersions.add("NeoForge")
                }

                // override environments if provided
                ext.supportedEnvironments.orNull?.let { mainFile.addEnvironment(*it.toTypedArray()) }

                ext.relationsRequired.orNull?.let { mainFile.addRequirement(*it.toTypedArray()) }
                ext.relationsOptional.orNull?.let { mainFile.addOptional(*it.toTypedArray()) }
                ext.relationsEmbedded.orNull?.let { mainFile.addEmbedded(*it.toTypedArray()) }
                ext.relationsTool.orNull?.let { mainFile.addTool(*it.toTypedArray()) }
                ext.relationsIncompatible.orNull?.let { mainFile.addIncompatibility(*it.toTypedArray()) }
            }

            mainTask.configure {
                dependsOn(publishTask)
            }
        }

        return mainTask
    }
}
