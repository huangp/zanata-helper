package org.zanata.helper.controller;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;
import org.zanata.helper.common.model.SyncType;
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

    private SyncType syncType = SyncType.SOURCE;

    @NotEmpty
    @Size(max = 255)
    private String sourceRepoExecutorName;

    @NotEmpty
    @Size(max = 255)
    private String translationServerExecutorName;

    private Map<String, String> sourceRepoConfig =
        new HashMap<String, String>();

    private Map<String, String> transServerConfig =
        new HashMap<String, String>();
}
