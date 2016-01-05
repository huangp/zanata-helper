package org.zanata.helper.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.ToString;

/**
 * @author Alex Eng <a href="aeng@redhat.com">aeng@redhat.com</a>
 */
@Getter
@ToString
public class JobConfig_test implements Serializable {

    private Long id;
    private String name;
    private String description;

    private SyncConfig syncToServerConfig;
    private SyncConfig syncToRepoConfig;

    private Map<String, String> sourceRepoConfig =
        new HashMap<String, String>();
    private Map<String, String> transServerConfig =
        new HashMap<String, String>();

    private String sourceRepoExecutorName;
    private String translationServerExecutorName;

    public JobConfig_test(Long id, String name, String description,
        SyncConfig syncToServerConfig, SyncConfig syncToRepoConfig,
        Map<String, String> sourceRepoConfig, String sourceRepoExecutorName,
        Map<String, String> transServerConfig,
        String translationServerExecutorName) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.syncToServerConfig = syncToServerConfig;
        this.syncToRepoConfig = syncToRepoConfig;
        this.sourceRepoConfig = sourceRepoConfig;
        this.sourceRepoExecutorName = sourceRepoExecutorName;
        this.transServerConfig = transServerConfig;
        this.translationServerExecutorName = translationServerExecutorName;
    }
}
