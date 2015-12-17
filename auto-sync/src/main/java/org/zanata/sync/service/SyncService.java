package org.zanata.sync.service;

import java.io.File;

import org.zanata.sync.exception.RepoSyncException;

public interface SyncService<P> {
    Credentials<P> getCredentials();

    void cloneRepo(String url, File destPath) throws RepoSyncException;
}
