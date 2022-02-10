package io.github.ladysnake.chenille

import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.revwalk.RevCommit
import org.eclipse.jgit.revwalk.RevSort
import org.eclipse.jgit.revwalk.RevWalk
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import java.io.File

class JGitWrapper(val dir: File) {
    val repository: Repository? by lazy { FileRepositoryBuilder().setGitDir(dir.resolve(".git")).build() }
    val firstCommit: RevCommit? by lazy { RevWalk(repository ?: return@lazy null).use {
        it.sort(RevSort.COMMIT_TIME_DESC, true)
        it.sort(RevSort.REVERSE, true)
        it.next()
    }}
}
