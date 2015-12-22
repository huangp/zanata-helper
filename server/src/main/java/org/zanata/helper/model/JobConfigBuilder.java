package org.zanata.helper.model;

import org.zanata.helper.controller.JobForm;

import lombok.NoArgsConstructor;

/**
 * @author Alex Eng <a href="mailto:aeng@redhat.com">aeng@redhat.com</a>
 */
@NoArgsConstructor
public class JobConfigBuilder {

    private String name;
    private String description;
    private String cron;
    private JobConfig.Type jobType;
    private JobConfig.SyncType syncType;
    private String sourceUrl;
    private String sourceUsername;
    private String sourceApiKey;
    private String zanataUrl;
    private String zanataUsername;
    private String zanataApiKey;

    public JobConfigBuilder(JobForm jobForm) {
        this.name = jobForm.getName();
        this.description = jobForm.getDescription();
        this.cron = jobForm.getCron();
        this.jobType = jobForm.getJobType();
        this.syncType = jobForm.getSyncType();
        this.sourceUrl = jobForm.getSourceUrl();
        this.sourceUsername = jobForm.getSourceUsername();
        this.sourceApiKey = jobForm.getSourceApiKey();
        this.zanataUrl = jobForm.getZanataUrl();
        this.zanataUsername = jobForm.getZanataUsername();
        this.zanataApiKey = jobForm.getZanataApiKey();
    }

    public JobConfigBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public JobConfigBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    public JobConfigBuilder setCron(String cron) {
        this.cron = cron;
        return this;
    }

    public JobConfigBuilder setJobType(JobConfig.Type jobType) {
        this.jobType = jobType;
        return this;
    }

    public JobConfigBuilder setSyncType(JobConfig.SyncType syncType) {
        this.syncType = syncType;
        return this;
    }

    public JobConfigBuilder setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
        return this;
    }

    public JobConfigBuilder setSourceUsername(String sourceUsername) {
        this.sourceUsername = sourceUsername;
        return this;
    }

    public JobConfigBuilder setSourceApiKey(String sourceApiKey) {
        this.sourceApiKey = sourceApiKey;
        return this;
    }

    public JobConfigBuilder setZanataUrl(String zanataUrl) {
        this.zanataUrl = zanataUrl;
        return this;
    }

    public JobConfigBuilder setZanataUsername(String zanataUsername) {
        this.zanataUsername = zanataUsername;
        return this;
    }

    public JobConfigBuilder setZanataApiKey(String zanataApiKey) {
        this.zanataApiKey = zanataApiKey;
        return this;
    }

    public JobConfig build() {
        return new JobConfig(name, description, jobType, syncType, sourceUrl,
                sourceUsername, sourceApiKey, zanataUrl, zanataUsername,
                zanataApiKey, cron);
    }
}
