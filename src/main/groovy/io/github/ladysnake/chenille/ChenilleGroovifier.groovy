package io.github.ladysnake.chenille

import kotlin.Unit
import kotlin.jvm.functions.Function1
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler

@SuppressWarnings('unused') // called through reflection because compilers suck
class ChenilleGroovifier {
    static void setupRepositoryExtensions(Project project, Map<String, Function1<RepositoryHandler, Unit>> exts) {
        exts.forEach({name, action ->
            project.repositories.ext[name] = {
                action(project.repositories)
            }
        })
    }
}
