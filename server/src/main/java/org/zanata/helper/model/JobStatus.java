package org.zanata.helper.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.zanata.helper.util.DateSerializer;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Alex Eng <a href="aeng@redhat.com">aeng@redhat.com</a>
 */
@Getter
@NoArgsConstructor
public class JobStatus implements Serializable {
    private JobStatusType status = JobStatusType.NONE;

    @JsonSerialize(using = DateSerializer.class)
    private Date lastStartTime;

    @JsonSerialize(using = DateSerializer.class)
    private Date lastEndTime;

    @JsonSerialize(using = DateSerializer.class)
    private Date nextStartTime;

    public JobStatus(JobStatusType status, Date lastStartTime, Date lastEndTime,
            Date nextStartTime) {
        this.status = status;
        this.lastStartTime = lastStartTime;
        this.lastEndTime = lastEndTime;
        this.nextStartTime = nextStartTime;
    }
}
