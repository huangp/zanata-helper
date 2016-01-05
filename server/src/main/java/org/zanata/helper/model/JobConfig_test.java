package org.zanata.helper.model;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Alex Eng <a href="aeng@redhat.com">aeng@redhat.com</a>
 */
@Getter
@NoArgsConstructor
public class JobConfig_test extends PersistModel {

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

    @Setter(AccessLevel.PROTECTED)
    private Date createdDate;

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
