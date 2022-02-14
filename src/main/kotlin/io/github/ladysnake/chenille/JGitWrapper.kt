package io.github.ladysnake.chenille

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.Status
import org.eclipse.jgit.lib.BranchTrackingStatus
import org.eclipse.jgit.lib.Constants
import org.eclipse.jgit.lib.Ref
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.revwalk.RevCommit
import org.eclipse.jgit.revwalk.RevSort
import org.eclipse.jgit.revwalk.RevWalk
import org.eclipse.jgit.transport.FetchResult

class JGitWrapper(val jgit: Git) {
    val repository: Repository by lazy { jgit.repository }
    val firstCommit: RevCommit by lazy { RevWalk(repository).use {
        it.sort(RevSort.COMMIT_TIME_DESC, true)
        it.sort(RevSort.REVERSE, true)
        it.next()
    }}

    fun status(): Status = jgit.status().call()

    fun trackingStatus(): BranchTrackingStatus? = currentBranch()?.let { BranchTrackingStatus.of(repository, it) }

    fun currentBranch(): String? {
            val head: Ref? = repository.exactRef(Constants.HEAD)
            if (head != null && head.isSymbolic) {
                return head.target.shortName
            }
            return null
        }

    fun fetch(): FetchResult = jgit.fetch().call()
    fun listTags(): List<Ref> = jgit.tagList().call()
}

val Ref.shortName: String get() = Repository.shortenRefName(name)
