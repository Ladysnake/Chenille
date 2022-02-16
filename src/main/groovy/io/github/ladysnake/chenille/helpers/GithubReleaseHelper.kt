package io.github.ladysnake.chenille.helpers

import com.github.breadmoirai.githubreleaseplugin.GithubReleaseExtension
import io.github.ladysnake.chenille.ChenilleProject
import net.fabricmc.loom.task.RemapJarTask

class GithubReleaseHelper(private val project: ChenilleProject) {
    fun configureDefaults() {
        project.extensions.configure(GithubReleaseExtension::class.java) {
            it.token("${project.findProperty("github_releases_token")}")
            // default owner: last component of maven group
            // default repo: name of the project
            it.setTagName(project.version.toString())
            project.git?.run { it.setTargetCommitish { currentBranch() } }
            it.setBody(project.changelog)

            it.setReleaseAssets(project.tasks.named("remapJar", RemapJarTask::class.java))
        }
    }
}
