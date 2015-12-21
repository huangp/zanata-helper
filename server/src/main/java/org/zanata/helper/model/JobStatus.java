package org.zanata.helper.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import org.zanata.helper.util.DateSerializer;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Alex Eng <a href="aeng@redhat.com">aeng@redhat.com</a>
 */
@Getter
public class JobStatus implements Serializable {
    private JobStatusType status;

    @JsonSerialize(using = DateSerializer.class)
    private Date lastExecutedTime;

    @JsonSerialize(using = DateSerializer.class)
    private Date nextExecuteTime;

    public JobStatus(JobStatusType status, Date lastExecutedTime, Date nextExecuteTime) {
        this.status = status;
        this.lastExecutedTime = lastExecutedTime;
        this.nextExecuteTime = nextExecuteTime;
    }
}
