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
import org.gradle.api.artifacts.dsl.RepositoryHandler
import java.util.function.Function

class RepositoryHandlerChenilleExtensionImpl(private val repositories: RepositoryHandler, private val extensions: Collection<Function<RepositoryHandler, Unit>>): RepositoryHandlerChenilleExtension {
    companion object {
        lateinit var instance: RepositoryHandlerChenilleExtension
    }

    override fun defaultRepositories() {
        extensions.forEach { it.apply(repositories) }
    }
}
