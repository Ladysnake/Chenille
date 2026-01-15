/*
 * Chenille
 * Copyright (C) 2022-2026 Ladysnake
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

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.Status
import org.eclipse.jgit.api.TransportCommand
import org.eclipse.jgit.api.errors.NoHeadException
import org.eclipse.jgit.lib.BranchTrackingStatus
import org.eclipse.jgit.lib.Constants
import org.eclipse.jgit.lib.ObjectId
import org.eclipse.jgit.lib.Ref
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.revwalk.RevCommit
import org.eclipse.jgit.revwalk.RevSort
import org.eclipse.jgit.revwalk.RevWalk
import org.eclipse.jgit.transport.FetchResult
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider

class JGitWrapper(val jgit: Git, gitAccessToken: String? = null) {
    val repository: Repository by lazy { jgit.repository }
    val firstCommit: RevCommit by lazy { RevWalk(repository).use {
        it.sort(RevSort.COMMIT_TIME_DESC, true)
        it.sort(RevSort.REVERSE, true)
        val headId: ObjectId = repository.resolve(Constants.HEAD) ?: throw NoHeadException("No repository head found")
        it.markStart(it.lookupCommit(headId))
        it.next()
    }}
    private val credentialsProvider = gitAccessToken?.let {
        UsernamePasswordCredentialsProvider(it, "")
    }

    fun status(): Status = jgit.status().call()

    fun trackingStatus(): BranchTrackingStatus? = currentBranch()?.let { BranchTrackingStatus.of(repository, it) }

    fun currentBranch(): String? {
            val head: Ref? = repository.exactRef(Constants.HEAD)
            if (head != null && head.isSymbolic) {
                return head.target.shortName
            }
            return null
        }

    private fun <T : TransportCommand<*, *>> T.setCredentials(): T {
        if (credentialsProvider != null) {
            setCredentialsProvider(credentialsProvider)
        }
        return this
    }

    fun fetch(): FetchResult = jgit.fetch().setCredentials().call()
    fun listTags(): List<Ref> = jgit.tagList().call()
}

val Ref.shortName: String get() = Repository.shortenRefName(name)
