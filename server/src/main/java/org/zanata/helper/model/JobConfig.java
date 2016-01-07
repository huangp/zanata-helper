package org.zanata.helper.model;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Alex Eng <a href="aeng@redhat.com">aeng@redhat.com</a>
 */
@Getter
@NoArgsConstructor
public class JobConfig extends PersistModel {

    private Long id;
    private String name;
    private String description;

    private SyncConfig syncToServerConfig;
    private SyncConfig syncToRepoConfig;

    private Map<String, String> sourceRepoConfig =
            new HashMap<>();
    private Map<String, String> transServerConfig =
            new HashMap<>();

    private String sourceRepoExecutorName;
    private String translationServerExecutorName;

    @Setter(AccessLevel.PROTECTED)
    private Date createdDate;

    public JobConfig(String name, String description,
            SyncConfig syncToServerConfig, SyncConfig syncToRepoConfig,
            Map<String, String> sourceRepoConfig, String sourceRepoExecutorName,
            Map<String, String> transServerConfig,
            String translationServerExecutorName) {
        this(JobIDGenerator.nextID(), name, description, syncToServerConfig,
                syncToRepoConfig, sourceRepoConfig, sourceRepoExecutorName,
                transServerConfig, translationServerExecutorName);
    }

    public JobConfig(Long id, String name, String description,
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JobConfig that = (JobConfig) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(name, that.name) &&
                Objects.equals(description, that.description) &&
                Objects
                        .equals(syncToServerConfig, that.syncToServerConfig) &&
                Objects.equals(syncToRepoConfig, that.syncToRepoConfig) &&
                Objects.equals(sourceRepoConfig, that.sourceRepoConfig) &&
                Objects
                        .equals(transServerConfig, that.transServerConfig) &&
                Objects.equals(sourceRepoExecutorName,
                        that.sourceRepoExecutorName) &&
                Objects.equals(translationServerExecutorName,
                        that.translationServerExecutorName) &&
                Objects.equals(createdDate, that.createdDate);
    }

    @Override
    public int hashCode() {
        return Objects
                .hash(id, name, description, syncToServerConfig,
                        syncToRepoConfig,
                        sourceRepoConfig, transServerConfig,
                        sourceRepoExecutorName,
                        translationServerExecutorName, createdDate);
    }

    public void setLastJobStatus(JobStatus status) {
        //TODO implement this
        //throw new UnsupportedOperationException("Implement me!");
    }
}
