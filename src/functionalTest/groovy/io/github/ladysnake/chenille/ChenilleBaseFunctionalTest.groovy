/*
 * Chenille
 * Copyright (C) 2022-2024 Ladysnake
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


import org.gradle.testkit.runner.GradleRunner
import spock.lang.Specification
import spock.lang.TempDir

class ChenilleBaseFunctionalTest extends Specification {
    @TempDir File testProjectDir
    File buildFile
    File propertiesFile

    def setup() {
        buildFile = new File(testProjectDir, 'build.gradle')
        buildFile << """
            plugins {
                id 'fabric-loom'
                id 'io.github.ladysnake.chenille'
            }
        """
        propertiesFile = new File(testProjectDir, "gradle.properties")
    }

    def dependencies() {
        """
            dependencies {
                minecraft "com.mojang:minecraft:1.18.2"
                mappings "net.fabricmc:yarn:1.18.2+build.1:v2"
                modImplementation "net.fabricmc:fabric-loader:0.13.3"
            }
        """
    }

    def "can setup artifactory publishing"() {
        buildFile << """
            chenille {
                configurePublishing {
                    withLadysnakeMaven()
                }
            }

            ${dependencies()}
        """
        propertiesFile << """
            artifactory_user = a
            artifactory_api_key = b
        """

        when:
        def result = GradleRunner.create()
            .withProjectDir(testProjectDir)
            .withPluginClasspath()
            .withArguments("--stacktrace")
            .withDebug(true)
            .build()

        then:
        !result.output.isEmpty()
    }
}
