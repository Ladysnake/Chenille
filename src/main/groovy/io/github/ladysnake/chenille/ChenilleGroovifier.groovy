/*
 * Chenille
 * Copyright (C) 2022 Ladysnake
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


import io.github.ladysnake.chenille.api.RepositoryHandlerChenilleExtension
import kotlin.Unit
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler

import java.util.function.Function

@SuppressWarnings('unused') // called through reflection because compilers suck
class ChenilleGroovifier {
    static void setupRepositoryExtensions(Project project, Map<String, Function<RepositoryHandler, Unit>> exts, RepositoryHandlerChenilleExtension chenilleExtension) {
        exts.forEach({name, action ->
            project.repositories.ext[name] = {
                action.apply(project.repositories)
            }
        })
        project.repositories.ext["chenille"] = chenilleExtension
    }
}
