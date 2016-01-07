/*
 * Copyright 2015, Red Hat, Inc. and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.zanata.helper.quartz;

import java.io.File;

import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zanata.helper.common.plugin.RepoExecutor;
import org.zanata.helper.common.plugin.TranslationServerExecutor;

/**
 * @author Patrick Huang <a href="mailto:pahuang@redhat.com">pahuang@redhat.com</a>
 */
public class RepoSyncJob extends SyncJob {
    private static final Logger log =
            LoggerFactory.getLogger(RepoSyncJob.class);

    private static final int syncToRepoTotalSteps = 5;

    @Override
    protected void doSync(RepoExecutor srcExecutor,
            TranslationServerExecutor transServerExecutor)
            throws JobExecutionException {
        if (srcExecutor == null || transServerExecutor == null) {
            log.info("No plugin in job. Skipping. {}", jobConfig.toString());
            return;
        }

        try {
            updateProgress(jobConfig.getId(), 1, syncToRepoTotalSteps,
                    "Sync to repository starts");
            File destDir = getDestDirectory(jobConfig.getId().toString());
            updateProgress(jobConfig.getId(),
                    2, syncToRepoTotalSteps, "Cloning repository to " + destDir);
            srcExecutor.cloneRepo(destDir);
            updateProgress(jobConfig.getId(),
                    3, syncToRepoTotalSteps,
                    "Pulling files to server from " + destDir);
            transServerExecutor
                    .pullFromServer(destDir, jobConfig.getSyncToServerConfig().getOption());
            updateProgress(jobConfig.getId(),
                    4, syncToRepoTotalSteps,
                    "Commits to repository from " + destDir);
            srcExecutor.pushToRepo(destDir, jobConfig.getSyncToRepoConfig().getOption());
            updateProgress(jobConfig.getId(), 5, syncToRepoTotalSteps,
                    "Sync to repository completed");
        } catch (Exception e) {
            throw new JobExecutionException(e);
        }
    }
}
