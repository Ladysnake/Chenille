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
package io.github.ladysnake.chenille

import io.github.ladysnake.chenille.api.ChenilleGradleExtension
import net.fabricmc.loom.LoomGradleExtension
import org.gradle.api.tasks.SourceSetContainer

open class ChenilleGradleExtensionImpl(private val project: ChenilleProject): ChenilleGradleExtension {
    override fun configureTestmod() {
        val sourceSets = project.extensions.getByType(SourceSetContainer::class.java)
        val main = sourceSets.getByName("main")
        val testmodSourceSet = sourceSets.create("testmod") { testmod ->
            testmod.compileClasspath += main.compileClasspath
            testmod.runtimeClasspath += main.runtimeClasspath
        }

        project.extensions.configure<LoomGradleExtension>("loom") { loom ->
            loom.runs {
                it.create("testmodClient") { run ->
                    run.client()
                    run.name("Testmod Client")
                    run.source(testmodSourceSet)
                }
                val testmodServer = it.create("testmodServer") { run ->
                    run.server()
                    run.name("Testmod Server")
                    run.source(testmodSourceSet)
                }
                it.create("gametest") { run ->
                    run.inherit(testmodServer)
                    run.name("Game Test")
                    // Enable the gametest runner
                    run.vmArg("-Dfabric-api.gametest")
                    run.vmArg("-Dfabric-api.gametest.report-file=${project.buildDir}/junit.xml")
                    run.runDir("build/gametest")
                }
                it.create("autoTestServer") { run ->
                    run.inherit(testmodServer)
                    run.name("Auto Test Server")
                    run.vmArg("-Dfabric.autoTest")
                }
                project.tasks.named("check") { check ->
                    check.dependsOn(project.tasks.named("runGametest"))
                }
            }
        }
    }
}
