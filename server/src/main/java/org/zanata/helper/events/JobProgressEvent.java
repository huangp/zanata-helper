package org.zanata.helper.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.zanata.helper.model.JobType;

/**
 * @author Alex Eng <a href="aeng@redhat.com">aeng@redhat.com</a>
 */
@Getter
@AllArgsConstructor
public class JobProgressEvent {
    private Long id;
    private JobType jobType;
    private int currentStep;
    private int totalSteps;
    private String description;
}
