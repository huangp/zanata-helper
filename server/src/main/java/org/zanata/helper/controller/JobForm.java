package org.zanata.helper.controller;

import java.io.Serializable;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.URL;
import org.zanata.helper.model.JobConfig;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Alex Eng <a href="mailto:aeng@redhat.com">aeng@redhat.com</a>
 */
@NoArgsConstructor
@Getter
@Setter
public class JobForm implements Serializable {

    @Size(max = 100)
    @NotEmpty
    private String name;

    @Size(max = 255)
    private String description;

    @Size(max = 50)
    private String cron;

    private JobConfig.Type jobType = JobConfig.Type.SYNC_TO_SERVER;

    private JobConfig.SyncType syncType = JobConfig.SyncType.SOURCE;

    @NotEmpty
    @Size(max = 2083)
    private String sourceUrl;

    @NotEmpty
    @Size(max = 100)
    private String sourceUsername;

    @NotEmpty
    @Size(max = 255)
    private String sourceApiKey;

    @NotEmpty
    @Size(max = 2083)
    @URL
    private String zanataUrl;

    @NotEmpty
    @Size(max = 100)
    private String zanataUsername;

    @NotEmpty
    @Size(max = 255)
    private String zanataApiKey;
}
