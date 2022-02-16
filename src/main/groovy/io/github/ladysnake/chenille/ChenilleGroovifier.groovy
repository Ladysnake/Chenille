package io.github.ladysnake.chenille

import kotlin.Unit
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler

import java.util.function.Function

@SuppressWarnings('unused') // called through reflection because compilers suck
class ChenilleGroovifier {
    static void setupRepositoryExtensions(Project project, Map<String, Function<RepositoryHandler, Unit>> exts) {
        exts.forEach({name, action ->
            project.repositories.ext[name] = {
                action.apply(project.repositories)
            }
        })
    }
}
