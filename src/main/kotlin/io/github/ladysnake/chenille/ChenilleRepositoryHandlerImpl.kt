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
package io.github.ladysnake.chenille

import io.github.ladysnake.chenille.api.ChenilleRepositoryHandler
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.kotlin.dsl.maven
import kotlin.reflect.full.declaredFunctions

class ChenilleRepositoryHandlerImpl(private val repositories: RepositoryHandler): ChenilleRepositoryHandler {
    override fun allCommonRepositories() {
        ChenilleRepositoryHandler::class.declaredFunctions.filter { it.name != this::allCommonRepositories.name }.forEach { it.call(this) }
    }

    private inline fun maven(name: String, url: String, crossinline configure: MavenArtifactRepository.() -> Unit = {}): MavenArtifactRepository {
        return repositories.maven(url) {
            this.name = name
            configure()
        }
    }

    override fun cursemaven() {
        repositories.exclusiveContent {
            forRepository {
                maven("Cursemaven", "https://cursemaven.com")
            }
            filter {
                includeGroup("curse.maven")
            }
        }
    }

    override fun jamieswhiteshirt() {
        maven(name = "maven.jamieswhiteshirt.com", url = "https://maven.jamieswhiteshirt.com/libs-release/") {
            mavenContent {
                includeGroup("com.jamieswhiteshirt")
                releasesOnly()
            }
        }
    }

    override fun jitpack() {
        maven("Jitpack", "https://jitpack.io") {
            mavenContent {
                includeGroupAndSubgroups("io.github")
                includeGroupAndSubgroups("com.github")
            }
        }
    }

    override fun ladysnake() {
        maven("Ladysnake Releases", "https://maven.ladysnake.org/releases") {
            mavenContent {
                includeGroupAndSubgroups("dev.emi")
                includeGroupByRegex(".*moriyashiine.*")
                includeGroup("io.github.edwinmindcraft") // Origins for Forge maven group
                includeGroupAndSubgroups("io.github.ladysnake")
                includeGroupAndSubgroups("org.ladysnake")
                includeGroupAndSubgroups("dev.doctor4t")
                includeGroupAndSubgroups("dev.onyxstudios")
                includeGroupByRegex("io.github.onyxstudios")
                releasesOnly()
            }
        }
    }

    override fun lucko() {
        maven("Lucko", "https://oss.sonatype.org/content/repositories/snapshots") {
            mavenContent {
                includeGroupAndSubgroups("me.lucko")
            }
        }
    }

    override fun modrinth() {
        repositories.exclusiveContent {
            forRepository {
                maven("Modrinth", "https://api.modrinth.com/maven")
            }
            filter {
                includeGroup("maven.modrinth")
            }
        }
    }

    override fun parchment() {
        repositories.exclusiveContent {
            forRepository {
                maven("Parchment", "https://maven.parchmentmc.org")
            }
            filter {
                includeGroupAndSubgroups("org.parchmentmc")
            }
        }
    }

    override fun shedaniel() {
        maven("maven.shedaniel.me", "https://maven.shedaniel.me/") {
            mavenContent {
                includeGroupAndSubgroups("me.shedaniel")
                includeGroupAndSubgroups("me.sargunvohra.mcmods")
                includeGroupAndSubgroups("dev.architectury")
            }
        }
    }

    override fun terraformers() {
        maven("TerraformersMC", "https://maven.terraformersmc.com/releases") {
            mavenContent {
                includeGroupAndSubgroups("com.terraformersmc")
                includeGroupAndSubgroups("dev.emi")
                releasesOnly()
            }
        }
    }

    override fun quiltMC() {
        maven("QuiltMC", "https://maven.quiltmc.org/repository/release") {
            mavenContent {
                releasesOnly()
            }
        }
    }

    override fun quiltMCSnapshot() {
        maven("QuiltMC Snapshot", "https://maven.quiltmc.org/repository/snapshot") {
            mavenContent {
                snapshotsOnly()
            }
        }
    }

    override fun up() {
        maven("Up mod releases", "https://maven.uuid.gg/releases")
    }
}
