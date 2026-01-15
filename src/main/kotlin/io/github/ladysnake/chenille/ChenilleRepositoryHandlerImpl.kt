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
import kotlin.reflect.full.declaredFunctions

class ChenilleRepositoryHandlerImpl(private val repositories: RepositoryHandler): ChenilleRepositoryHandler {
    override fun allCommonRepositories() {
        ChenilleRepositoryHandler::class.declaredFunctions.filter { it.name != this::allCommonRepositories.name }.forEach { it.call(this) }
    }

    private inline fun maven(name: String, url: String, crossinline configure: MavenArtifactRepository.() -> Unit = {}): MavenArtifactRepository {
        return repositories.maven { repo ->
            repo.name = name
            repo.setUrl(url)
            repo.configure()
        }
    }

    override fun cotton() {
        maven(name = "CottonMC", url = "https://server.bbkr.space/artifactory/libs-release") {
            mavenContent {
                it.includeGroup("io.github.cottonmc")
                it.releasesOnly()
            }
        }
    }

    override fun cursemaven() {
        repositories.exclusiveContent { exclusive ->
            exclusive.forRepository {
                maven("Cursemaven", "https://cursemaven.com")
            }
            exclusive.filter {
                it.includeGroup("curse.maven")
            }
        }
    }

    override fun jamieswhiteshirt() {
        maven(name = "maven.jamieswhiteshirt.com", url = "https://maven.jamieswhiteshirt.com/libs-release/") {
            mavenContent {
                it.includeGroup("com.jamieswhiteshirt")
                it.releasesOnly()
            }
        }
    }

    override fun jitpack() {
        maven("Jitpack", "https://jitpack.io") {
            mavenContent {
                it.includeGroupByRegex("(io|com)\\.github\\..*")
            }
        }
    }

    override fun ladysnake() {
        maven("Ladysnake Releases", "https://maven.ladysnake.org/releases") {
            mavenContent {
                it.includeGroupAndSubgroups("dev.emi")
                it.includeGroupByRegex(".*moriyashiine.*")
                it.includeGroup("io.github.edwinmindcraft") // Origins for Forge maven group
                it.includeGroup("io.github.ladysnake")
                it.includeGroupAndSubgroups("org.ladysnake")
                it.includeGroupAndSubgroups("dev.doctor4t")
                it.includeGroupByRegex("(dev|io\\.github)\\.onyxstudios.*")
                it.releasesOnly()
            }
        }
    }

    override fun lucko() {
        maven("Lucko", "https://oss.sonatype.org/content/repositories/snapshots") {
            mavenContent {
                it.includeGroupAndSubgroups("me.lucko")
            }
        }
    }

    override fun modrinth() {
        repositories.exclusiveContent { exclusive ->
            exclusive.forRepository {
                maven("Modrinth", "https://api.modrinth.com/maven")
            }
            exclusive.filter {
                it.includeGroup("maven.modrinth")
            }
        }
    }

    override fun parchment() {
        repositories.exclusiveContent { exclusive ->
            exclusive.forRepository {
                maven("Parchment", "https://maven.parchmentmc.org")
            }
            exclusive.filter {
                it.includeGroupAndSubgroups("org.parchmentmc")
            }
        }
    }

    override fun shedaniel() {
        maven("maven.shedaniel.me", "https://maven.shedaniel.me/") {
            mavenContent {
                it.includeGroupAndSubgroups("me.shedaniel")
                it.includeGroupAndSubgroups("me.sargunvohra.mcmods")
                it.includeGroupAndSubgroups("dev.architectury")
            }
        }
    }

    override fun terraformers() {
        maven("TerraformersMC", "https://maven.terraformersmc.com/releases") {
            mavenContent {
                it.includeGroupAndSubgroups("com.terraformersmc")
                it.includeGroupAndSubgroups("dev.emi")
                it.releasesOnly()
            }
        }
    }

    override fun quiltMC() {
        maven("QuiltMC", "https://maven.quiltmc.org/repository/release") {
            mavenContent {
                it.releasesOnly()
            }
        }
    }

    override fun quiltMCSnapshot() {
        maven("QuiltMC Snapshot", "https://maven.quiltmc.org/repository/snapshot") {
            mavenContent {
                it.snapshotsOnly()
            }
        }
    }

    override fun up() {
        maven("Up mod releases", "https://maven.uuid.gg/releases")
    }
}
