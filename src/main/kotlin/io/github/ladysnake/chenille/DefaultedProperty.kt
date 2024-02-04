/*
 * Chenille
 * Copyright (C) 2022-2024 Ladysnake
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

import kotlin.reflect.KProperty

fun <T> defaulted(initializer: () -> T) = DefaultedProperty(initializer)

class DefaultedProperty<T>(val initializer: () -> T) {
    private var listener: (T) -> Unit = {}
    private var initialized: Boolean = false
    private var value: T? = null

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        if (!initialized) this.setValue(thisRef, property, initializer())
        @Suppress("UNCHECKED_CAST") // cannot be null at this point unless the caller wants it to be
        return this.value as T
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        initialized = true
        this.value = value
        this.listener(value)
    }

    infix fun withListener(listener: (T) -> Unit): DefaultedProperty<T> {
        val oldListener = this.listener
        this.listener = {
            oldListener(it)
            listener(it)
        }
        return this
    }
}
