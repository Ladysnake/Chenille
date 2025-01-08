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
            repo.content {
                it.includeGroup("io.github.cottonmc")
            }
        }
    }

    override fun cursemaven() {
        repositories.maven { repo ->
            repo.name = "Cursemaven"
            repo.setUrl("https://cursemaven.com")
            repo.content {
                it.includeGroup("curse.maven")
            }
        }
    }

    override fun jamieswhiteshirt() {
        repositories.maven { repo ->
            repo.setUrl("https://maven.jamieswhiteshirt.com/libs-release/")
            repo.content {
                it.includeGroup("com.jamieswhiteshirt")
            }
        }
    }

    override fun jitpack() {
        repositories.maven { repo ->
            repo.name = "Jitpack"
            repo.setUrl("https://jitpack.io")
            repo.content {
                it.includeGroupByRegex("(io|com)\\.github\\..*")
            }
        }
    }

    override fun ladysnake() {
        repositories.maven { repo ->
            repo.name = "Ladysnake Releases"
            repo.setUrl("https://maven.ladysnake.org/releases")
            repo.content {
                it.includeGroupByRegex("dev\\.emi.*")
                it.includeGroupByRegex(".*moriyashiine.*")
                it.includeGroup("io.github.edwinmindcraft") // Origins for Forge maven group
                it.includeGroup("io.github.ladysnake")
                it.includeGroupByRegex("org.ladysnake.*")
                it.includeGroup("dev.doctor4t")
                it.includeGroupByRegex("(dev|io\\.github)\\.onyxstudios.*")
            }
        }
    }

    override fun lucko() {
        repositories.maven { repo ->
            repo.name = "Lucko"
            repo.setUrl("https://oss.sonatype.org/content/repositories/snapshots")
            repo.content {
                it.includeGroup("me.lucko")
            }
        }
    }

    override fun modrinth() {
        repositories.maven { repo ->
            repo.name = "Modrinth"
            repo.setUrl("https://api.modrinth.com/maven")
            repo.content {
                it.includeGroup("maven.modrinth")
            }
        }
    }

    override fun parchment() {
        repositories.maven { repo ->
            repo.name = "Parchment"
            repo.setUrl("https://maven.parchmentmc.org")
            repo.content {
                it.includeGroup("org.parchmentmc")
            }
        }
    }

    override fun shedaniel() {
        repositories.maven { repo ->
            repo.setUrl("https://maven.shedaniel.me/")
            repo.content {
                it.includeGroupByRegex("me\\.shedaniel\\..*")
                it.includeGroup("me.sargunvohra.mcmods")
                it.includeGroup("dev.architectury")
            }
        }
    }

    override fun terraformers() {
        repositories.maven { repo ->
            repo.name = "TerraformersMC"
            repo.setUrl("https://maven.terraformersmc.com/releases")
            repo.content {
                it.includeGroup("com.terraformersmc")
                it.includeGroup("dev.emi")
            }
        }
    }

    override fun quiltMC() {
        repositories.maven { repo ->
            repo.name = "QuiltMC"
            repo.setUrl("https://maven.quiltmc.org/repository/release")
        }
    }

    override fun quiltMCSnapshot() {
        repositories.maven { repo ->
            repo.name = "QuiltMC Snapshot"
            repo.setUrl("https://maven.quiltmc.org/repository/snapshot")
        }
    }
}
