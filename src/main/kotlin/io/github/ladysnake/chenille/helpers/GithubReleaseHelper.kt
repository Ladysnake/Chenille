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
import org.gradle.api.logging.Logger
import org.gradle.kotlin.dsl.assign
import org.jetbrains.annotations.VisibleForTesting

internal object GithubReleaseHelper {

    val repositoryRegex =
        Regex("^(?<host>(?:git@|https://)[\\w.@]+)[/:](?<owner>[\\w_-]+)/(?<repo>[\\w_-]+)(?:.git)?/?$")

    fun configureDefaults(project: ChenilleProject, cfg: PublishingConfiguration) {
        project.pluginManager.apply("com.github.breadmoirai.github-release")

        project.extensions.configure(GithubReleaseExtension::class.java) {
            setToken(project.providers.gradleProperty("github_api_key"))
            // default owner: last component of maven group
            // default repo: name of the project
            val repositoryUrl = project.extension.github?.toString()
            if (repositoryUrl == null || extractRepositoryProperties(this, repositoryUrl, project.logger)) return@configure

            tagName = project.version.toString()
            targetCommitish = project.git?.currentBranch()
            body = project.changelog.call().toString()

            setReleaseAssets(cfg.mainArtifact)
        }
    }

    @VisibleForTesting
    fun extractRepositoryProperties(
            ext: GithubReleaseExtension, repositoryUrl: String, logger: Logger
    ): Boolean {
        val matchResult = repositoryRegex.matchEntire(repositoryUrl) ?: error("Not a valid git URL $repositoryUrl")
        val host = matchResult.groups["host"]!!.value
        val repoOwner = matchResult.groups["owner"]!!.value
        val repoName = matchResult.groups["repo"]!!.value

        logger.debug("Found Host: {} Owner: {} Repo: {}", host, repoOwner, repoName)

        if (!host.contains("github.com")) {
            logger.lifecycle("Unable to configure github release: Repository host is not GitHub! (found ${host})")
            return true
        }

        ext.owner = repoOwner
        ext.repo = repoName
        return false
    }
}
