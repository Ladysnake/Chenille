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

import com.github.breadmoirai.githubreleaseplugin.GithubReleaseExtension
import io.github.ladysnake.chenille.ChenilleProject
import io.github.ladysnake.chenille.api.PublishingConfiguration
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.the

internal object GithubReleaseHelper {

    val repositoryRegex = Regex("^(?<host>(?:git@|https://)[\\w.@]+)[/:](?<owner>[\\w_-]+)/(?<repo>[\\w_-]+)(?:.git)?/?$")

    fun configureDefaults(project: ChenilleProject, cfg: PublishingConfiguration) {
        project.pluginManager.apply("com.github.breadmoirai.github-release")

        project.extensions.configure(GithubReleaseExtension::class.java) {
            setToken(project.providers.gradleProperty("github_api_key"))
            // default owner: last component of maven group
            // default repo: name of the project
            project.extension.github
                ?.let { repositoryRegex.matchEntire(it.toString()) }
                ?.let { matchResult ->
                    val host = matchResult.groups["owner"]!!.value
                    val repoOwner = matchResult.groups["owner"]!!.value
                    val repoName = matchResult.groups["repo"]!!.value

                    project.logger.debug("Found Host: {} Owner: {} Repo: {}", host, repoOwner, repoName)

                    if(host.contains("github.com")) {
                        project.logger.lifecycle("Unable to configure github release: Repository host is not GitHub! (found ${host})")
                        return@configure
                    }

                    owner = repoOwner
                    repo = repoName
                }

            tagName = project.version.toString()
            targetCommitish = project.git?.currentBranch()
            body = project.changelog.call().toString()

            setReleaseAssets(cfg.mainArtifact)
        }
    }
}
