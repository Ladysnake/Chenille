package io.github.ladysnake.chenille.helpers

import io.github.ladysnake.chenille.ChenilleProject
import org.jfrog.gradle.plugin.artifactory.dsl.ArtifactoryPluginConvention

class ArtifactoryHelper(private val project: ChenilleProject) {
    fun configureDefaults() {
        if (project.hasProperty("artifactory_user")) {
            @Suppress("DEPRECATION")    // Artifactory bad >:(
            project.convention.getPlugin(ArtifactoryPluginConvention::class.java).let {
                if (project.isLadysnakeProject()) it.setContextUrl("https://ladysnake.jfrog.io/artifactory/")
                it.publish { cfg ->
                    cfg.repository { repo ->
                        if (project.isLadysnakeProject()) repo.setRepoKey("mods")

                        repo.setUsername(project.findProperty("artifactory_user"))
                        repo.setPassword(project.findProperty("artifactory_api_key"))
                    }
                    cfg.defaults { def ->
                        def.publications("mavenJava")

                        def.setPublishArtifacts(true)
                        def.setPublishPom(true)
                    }
                }
            }
        } else {
            println("Artifactory not configured; please define the 'artifactory_user' and 'artifactory_api_key' properties before running artifactoryPublish")
        }
    }
}
