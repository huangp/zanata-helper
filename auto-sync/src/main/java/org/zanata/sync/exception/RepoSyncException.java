package org.zanata.sync.exception;

/**
 * @author Patrick Huang <a href="mailto:pahuang@redhat.com">pahuang@redhat.com</a>
 */
public class RepoSyncException extends RuntimeException {
    public RepoSyncException() {
        super("failed to clone source repository");
    }

    public RepoSyncException(Exception e) {
        super("failed to clone source repository", e);
    }
}
