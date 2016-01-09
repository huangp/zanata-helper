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
public class SyncWorkConfig extends PersistModel {

    private Long id;
    private String name;
    private String description;

    private JobConfig syncToServerConfig;
    private JobConfig syncToRepoConfig;

    private Map<String, String> srcRepoPluginConfig =
            new HashMap<>();
    private Map<String, String> transServerConfig =
            new HashMap<>();

    private String srcRepoPluginName;
    private String transServerPluginName;

    @Setter(AccessLevel.PROTECTED)
    private Date createdDate;

    public SyncWorkConfig(String name, String description,
            JobConfig syncToServerConfig, JobConfig syncToRepoConfig,
            Map<String, String> srcRepoPluginConfig, String srcRepoPluginName,
            Map<String, String> transServerConfig,
            String transServerPluginName) {
        this(SyncWorkIDGenerator.nextID(), name, description, syncToServerConfig,
                syncToRepoConfig, srcRepoPluginConfig, srcRepoPluginName,
                transServerConfig, transServerPluginName);
    }

    public SyncWorkConfig(Long id, String name, String description,
            JobConfig syncToServerConfig, JobConfig syncToRepoConfig,
            Map<String, String> srcRepoPluginConfig, String srcRepoPluginName,
            Map<String, String> transServerConfig,
            String transServerPluginName) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.syncToServerConfig = syncToServerConfig;
        this.syncToRepoConfig = syncToRepoConfig;
        this.srcRepoPluginConfig = srcRepoPluginConfig;
        this.srcRepoPluginName = srcRepoPluginName;
        this.transServerConfig = transServerConfig;
        this.transServerPluginName = transServerPluginName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SyncWorkConfig that = (SyncWorkConfig) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(name, that.name) &&
                Objects.equals(description, that.description) &&
                Objects
                        .equals(syncToServerConfig, that.syncToServerConfig) &&
                Objects.equals(syncToRepoConfig, that.syncToRepoConfig) &&
                Objects.equals(srcRepoPluginConfig, that.srcRepoPluginConfig) &&
                Objects
                        .equals(transServerConfig, that.transServerConfig) &&
                Objects.equals(srcRepoPluginName,
                        that.srcRepoPluginName) &&
                Objects.equals(transServerPluginName,
                        that.transServerPluginName) &&
                Objects.equals(createdDate, that.createdDate);
    }

    @Override
    public int hashCode() {
        return Objects
                .hash(id, name, description, syncToServerConfig,
                        syncToRepoConfig,
                    srcRepoPluginConfig, transServerConfig,
                    srcRepoPluginName,
                    transServerPluginName, createdDate);
    }

    public void setLastJobStatus(JobStatus status, JobType type) {
        if (type.equals(JobType.REPO_SYNC)) {
            this.syncToRepoConfig
                .updateStatus(status.getStatus(), status.getLastStartTime(),
                    status.getLastEndTime(), status.getNextStartTime());
        } else if (type.equals(JobType.SERVER_SYNC)) {
            this.syncToServerConfig
                .updateStatus(status.getStatus(), status.getLastStartTime(),
                    status.getLastEndTime(), status.getNextStartTime());
        }
    }
}
