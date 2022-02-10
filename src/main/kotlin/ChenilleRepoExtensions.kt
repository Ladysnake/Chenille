@file:JvmName("ChenilleRepoExtensions")

import org.gradle.api.artifacts.dsl.RepositoryHandler

fun RepositoryHandler.cursemaven() {
    maven { repo ->
        repo.setUrl("https://cursemaven.com")
        repo.content {
            it.includeGroup("curse.maven")
        }
    }
}

fun RepositoryHandler.jitpack() {
    maven { repo ->
        repo.setUrl("https://jitpack.io")
        repo.content {
            it.includeGroupByRegex("(io|com)\\.github\\..*")
        }
    }
}

fun RepositoryHandler.ladysnake() {
    maven { repo ->
        repo.name = "Ladysnake Mods"
        repo.setUrl("https://ladysnake.jfrog.io/artifactory/mods")
        repo.content {
            it.includeGroup("io.github.ladysnake")
            it.includeGroupByRegex("(dev|io\\.github)\\.onyxstudios.*")
        }
    }
}

fun RepositoryHandler.lucko() {
    maven { repo ->
        repo.setUrl("https://oss.sonatype.org/content/repositories/snapshots")
        repo.content {
            it.includeGroup("me.lucko")
        }
    }
}

fun RepositoryHandler.terraformers() {
    maven { repo ->
        repo.name = "TerraformersMC"
        repo.setUrl("https://maven.terraformersmc.com/releases")
        repo.content {
            it.includeGroup("com.terraformersmc")
        }
    }
}
