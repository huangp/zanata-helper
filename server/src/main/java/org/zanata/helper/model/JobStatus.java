package org.zanata.helper.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.NoArgsConstructor;

import org.zanata.helper.util.DateSerializer;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Alex Eng <a href="aeng@redhat.com">aeng@redhat.com</a>
 */
@Getter
@NoArgsConstructor
public class JobStatus implements Serializable {
    public static JobStatus EMPTY = new JobStatus();

    private JobStatusType status = JobStatusType.NONE;

    @JsonSerialize(using = DateSerializer.class)
    private Date lastStartTime;

    @JsonSerialize(using = DateSerializer.class)
    private Date lastEndTime;

    @JsonSerialize(using = DateSerializer.class)
    private Date nextStartTime;

    //This field is being ignored. See {@link SyncWorkConfigRepresenter}
    private JobProgress currentProgress = null;

    public JobStatus(JobStatusType status, Date lastStartTime, Date lastEndTime,
            Date nextStartTime) {
        this.status = status;
        this.lastStartTime = lastStartTime;
        this.lastEndTime = lastEndTime;
        this.nextStartTime = nextStartTime;
    }

    public JobStatus(JobStatusType status, Date lastStartTime, Date lastEndTime,
        Date nextStartTime, JobProgress currentProgress) {
        this(status, lastStartTime, lastEndTime, nextStartTime);
        this.currentProgress = currentProgress;
    }
}
