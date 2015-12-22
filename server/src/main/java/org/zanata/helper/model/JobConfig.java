package org.zanata.helper.model;

import java.io.Serializable;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.zanata.helper.common.SyncType;
import org.zanata.helper.common.plugin.SourceRepoExecutor;
import org.zanata.helper.common.plugin.TranslationServerExecutor;

/**
 * @author Alex Eng <a href="aeng@redhat.com">aeng@redhat.com</a>
 */
@Getter
@ToString
public class JobConfig implements Serializable {

    private Long id;
    private String name;
    private String description;
    /**
     * see http://en.wikipedia.org/wiki/Cron#CRON_expression
     */
    private String cron;
    private JobConfig.Type jobType;
    private SyncType syncType;
    private String sourceUrl;
    private String sourceUsername;
    private String sourceApiKey;
    private String zanataUrl;
    private String zanataUsername;
    private String zanataApiKey;

    @Setter
    private JobStatus lastJobStatus;

    private SourceRepoExecutor sourceRepoExecutor;
    private TranslationServerExecutor translationServerExecutor;

    public JobConfig(String name, String description, JobConfig.Type jobType,
            SyncType syncType, String sourceUrl,
            String sourceUsername, String sourceApiKey, String zanataUrl,
            String zanataUsername, String zanataApiKey, String cron) {
        this(UUID.randomUUID().timestamp(), name, description, jobType,
                syncType, sourceUrl, sourceUsername, sourceApiKey,
                zanataUrl, zanataUsername, zanataApiKey, cron);
    }

    public JobConfig(Long id, String name, String description,
            JobConfig.Type jobType, SyncType syncType,
            String sourceUrl, String sourceUsername, String sourceApiKey,
            String zanataUrl, String zanataUsername, String zanataApiKey,
            String cron) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.jobType = jobType;
        this.syncType = syncType;
        this.sourceUrl = sourceUrl;
        this.sourceUsername = sourceUsername;
        this.sourceApiKey = sourceApiKey;
        this.zanataUrl = zanataUrl;
        this.zanataUsername = zanataUsername;
        this.zanataApiKey = zanataApiKey;
        this.cron = cron;
    }

    public static enum Type {
        SYNC_TO_SERVER,
        SYNC_TO_REPO;
    }
}
