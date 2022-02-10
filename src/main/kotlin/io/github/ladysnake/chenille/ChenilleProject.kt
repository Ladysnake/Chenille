package io.github.ladysnake.chenille

import org.gradle.api.Project

class ChenilleProject(private val project: Project): Project by project {
    val git by lazy { JGitWrapper(rootDir) }
    val changelog = ChangelogText(
        project.file("changelog.md").toPath(),
        currentVersion = project.properties["mod_version"].toString(),
        changelogBaseUrl = project.properties["changelog_base_url"].toString()
    )
}
