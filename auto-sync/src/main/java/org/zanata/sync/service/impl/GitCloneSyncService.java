package org.zanata.sync.service.impl;

import java.io.File;

import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.zanata.sync.exception.RepoSyncException;
import org.zanata.sync.service.Credentials;
import org.zanata.sync.service.SyncService;

public class GitCloneSyncService implements SyncService<String> {

    private final Credentials<String> credentials;

    // TODO use CDI injection
    public GitCloneSyncService(Credentials<String> credentials) {
        this.credentials = credentials;
    }

    public Credentials<String> getCredentials() {
        return credentials;
    }

    public void cloneRepo(String url, File destPath) throws RepoSyncException {
        CloneCommand clone = Git.cloneRepository();
        clone.setBare(false);
        clone.setCloneAllBranches(true);
        clone.setDirectory(destPath).setURI(url);
        UsernamePasswordCredentialsProvider user =
                new UsernamePasswordCredentialsProvider(
                        getCredentials().getUsername(),
                        getCredentials().getSecret());
        clone.setCredentialsProvider(user);
        try {
            clone.call();
        }
        catch (GitAPIException e) {
            throw new RepoSyncException(e);
        }
    }
}
