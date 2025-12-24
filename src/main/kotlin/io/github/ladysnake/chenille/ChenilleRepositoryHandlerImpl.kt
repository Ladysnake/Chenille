/*
 * Chenille
 * Copyright (C) 2022-2025 Ladysnake
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
import kotlin.reflect.full.declaredFunctions

class ChenilleRepositoryHandlerImpl(private val repositories: RepositoryHandler): ChenilleRepositoryHandler {
    override fun allCommonRepositories() {
        this::class.declaredFunctions.filter { it.name != this::allCommonRepositories.name }.forEach { it.call(this) }
    }

    override fun cotton() {
        repositories.maven { repo ->
            repo.name = "CottonMC"
            repo.setUrl("https://server.bbkr.space/artifactory/libs-release")
            repo.mavenContent {
                it.includeGroup("io.github.cottonmc")
                it.releasesOnly()
            }
        }
    }

    override fun cursemaven() {
        repositories.exclusiveContent { exclusive ->
            exclusive.forRepositories(repositories.maven { repo ->
                repo.name = "Cursemaven"
                repo.setUrl("https://cursemaven.com")
            })
            exclusive.filter {
                it.includeGroup("curse.maven")
            }
        }
    }

    override fun jamieswhiteshirt() {
        repositories.maven { repo ->
            repo.setUrl("https://maven.jamieswhiteshirt.com/libs-release/")
            repo.mavenContent {
                it.includeGroup("com.jamieswhiteshirt")
                it.releasesOnly()
            }
        }
    }

    override fun jitpack() {
        repositories.maven { repo ->
            repo.name = "Jitpack"
            repo.setUrl("https://jitpack.io")
            repo.mavenContent {
                it.includeGroupByRegex("(io|com)\\.github\\..*")
            }
        }
    }

    override fun ladysnake() {
        repositories.maven { repo ->
            repo.name = "Ladysnake Releases"
            repo.setUrl("https://maven.ladysnake.org/releases")
            repo.mavenContent {
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
        repositories.maven { repo ->
            repo.name = "Lucko"
            repo.setUrl("https://oss.sonatype.org/content/repositories/snapshots")
            repo.mavenContent {
                it.includeGroupAndSubgroups("me.lucko")
            }
        }
    }

    override fun modrinth() {
        repositories.exclusiveContent { exclusive ->
            exclusive.forRepositories(repositories.maven { repo ->
                repo.name = "Modrinth"
                repo.setUrl("https://api.modrinth.com/maven")
            })
            exclusive.filter {
                it.includeGroup("maven.modrinth")
            }
        }
    }

    override fun parchment() {
        repositories.exclusiveContent { exclusive ->
            exclusive.forRepositories(repositories.maven { repo ->
                repo.name = "Parchment"
                repo.setUrl("https://maven.parchmentmc.org")
            })
            exclusive.filter {
                it.includeGroupAndSubgroups("org.parchmentmc")
            }
        }
    }

    override fun shedaniel() {
        repositories.maven { repo ->
            repo.setUrl("https://maven.shedaniel.me/")
            repo.mavenContent {
                it.includeGroupAndSubgroups("me.shedaniel")
                it.includeGroupAndSubgroups("me.sargunvohra.mcmods")
                it.includeGroupAndSubgroups("dev.architectury")
            }
        }
    }

    override fun terraformers() {
        repositories.maven { repo ->
            repo.name = "TerraformersMC"
            repo.setUrl("https://maven.terraformersmc.com/releases")
            repo.mavenContent {
                it.includeGroupAndSubgroups("com.terraformersmc")
                it.includeGroupAndSubgroups("dev.emi")
                it.releasesOnly()
            }
        }
    }

    override fun quiltMC() {
        repositories.maven { repo ->
            repo.name = "QuiltMC"
            repo.setUrl("https://maven.quiltmc.org/repository/release")
            repo.mavenContent {
                it.releasesOnly()
            }
        }
    }

    override fun quiltMCSnapshot() {
        repositories.maven { repo ->
            repo.name = "QuiltMC Snapshot"
            repo.setUrl("https://maven.quiltmc.org/repository/snapshot")
            repo.mavenContent {
                it.snapshotsOnly()
            }
        }
    }

    override fun up() {
        repositories.maven { repo ->
            repo.name = "Up mod releases"
            repo.setUrl("https://maven.uuid.gg/releases")
        }
    }
}
