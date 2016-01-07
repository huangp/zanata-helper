package org.zanata.helper.model;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author Alex Eng <a href="mailto:aeng@redhat.com">aeng@redhat.com</a>
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class JobSummary implements Serializable {
    private String key;
    private String name;
    private String description;
    private JobConfig.Type type;
    private JobStatus jobStatus;
}
