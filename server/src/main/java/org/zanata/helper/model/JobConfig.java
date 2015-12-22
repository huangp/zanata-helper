package org.zanata.helper.model;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Alex Eng <a href="mailto:aeng@redhat.com">aeng@redhat.com</a>
 */
public interface JobConfig extends Serializable {

    Type getType();

    Long getId();

    String getName();

    String getDescription();

    String getCron();

    String getSourceRepositoryUrl();

    String getZanataVersionUrl();

    JobStatus getLastJobStatus();

    void setLastJobStatus(JobStatus jobStatus);

    static enum Type {
        SYNC_TO_ZANATA,
        SYNC_TO_REPO;
    }
}
