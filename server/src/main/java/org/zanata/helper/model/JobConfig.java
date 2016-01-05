package org.zanata.helper.model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.zanata.helper.common.model.SyncOption;

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
    private SyncOption syncOption;
    private Map<String, String> sourceRepoConfig =
        new HashMap<String, String>();
    private Map<String, String> transServerConfig =
        new HashMap<String, String>();

    private String sourceRepoExecutorName;
    private String translationServerExecutorName;

    @Setter
    private JobStatus lastJobStatus;


    public JobConfig(String name, String description, JobConfig.Type jobType,
        SyncOption syncOption, Map<String, String> sourceRepoConfig,
        String sourceRepoExecutorName, Map<String, String> transServerConfig,
        String translationServerExecutorName, String cron) {
        this(new Date().getTime(), name, description, jobType,
            syncOption, sourceRepoConfig, sourceRepoExecutorName,
            transServerConfig, translationServerExecutorName, cron);
    }

    public JobConfig(Long id, String name, String description,
        JobConfig.Type jobType, SyncOption syncOption,
        Map<String, String> sourceRepoConfig, String sourceRepoExecutorName,
        Map<String, String> transServerConfig,
        String translationServerExecutorName, String cron) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.jobType = jobType;
        this.syncOption = syncOption;
        this.sourceRepoConfig = sourceRepoConfig;
        this.sourceRepoExecutorName = sourceRepoExecutorName;
        this.transServerConfig = transServerConfig;
        this.translationServerExecutorName = translationServerExecutorName;
        this.cron = cron;
    }

    public static enum Type {
        SYNC_TO_SERVER,
        SYNC_TO_REPO;
    }
}
