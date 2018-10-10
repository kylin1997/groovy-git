package org.xbib.groovy.git.operation

import java.util.concurrent.Callable

import org.xbib.groovy.git.Repository
import org.xbib.groovy.git.auth.TransportOpUtil
import org.xbib.groovy.git.internal.Operation
import org.eclipse.jgit.api.FetchCommand
import org.eclipse.jgit.transport.RefSpec
import org.eclipse.jgit.transport.TagOpt

/**
 * Fetch changes from remotes.
 */
@Operation('fetch')
class FetchOp implements Callable<Void> {
    private final Repository repo

    /**
     * Which remote should be fetched.
     */
    String remote

    /**
     * List of refspecs to fetch.
     */
    List refSpecs = []

    /**
     * {@code true} if branches removed by the remote should be
     * removed locally.
     */
    boolean prune = false

    /**
     * How should tags be handled.
     */
    TagMode tagMode = TagMode.AUTO

    FetchOp(Repository repo) {
        this.repo = repo
    }

    /**
     * Provides a string conversion to the enums.
     */
    void setTagMode(String mode) {
        tagMode = mode.toUpperCase()
    }

    Void call() {
        FetchCommand cmd = repo.jgit.fetch()
        TransportOpUtil.configure(cmd, repo.credentials)
        if (remote) { cmd.remote = remote }
        cmd.refSpecs = refSpecs.collect {
            new RefSpec(it)
        }
        cmd.removeDeletedRefs = prune
        cmd.tagOpt = tagMode.jgit
        cmd.call()
        return null
    }

    enum TagMode {
        AUTO(TagOpt.AUTO_FOLLOW),
        ALL(TagOpt.FETCH_TAGS),
        NONE(TagOpt.NO_TAGS)

        final TagOpt jgit

        private TagMode(TagOpt opt) {
            this.jgit = opt
        }
    }
}
