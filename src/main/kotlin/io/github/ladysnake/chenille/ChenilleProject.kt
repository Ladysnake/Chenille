package io.github.ladysnake.chenille

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.errors.RepositoryNotFoundException
import org.gradle.api.Project

class ChenilleProject(private val project: Project): Project by project {
    val git: JGitWrapper? by lazy {
        try { JGitWrapper(Git.open(rootDir)) } catch (e: RepositoryNotFoundException) { null }
    }
    val changelog = ChangelogText(
        project.file("changelog.md").toPath(),
        currentVersion = project.properties["mod_version"].toString(),
        changelogBaseUrl = project.properties["changelog_base_url"].toString()
    )
    fun isLadysnakeProject() = project.group.toString().takeIf { it.contains("ladysnake") || it.contains("onyxstudios") } != null
}
