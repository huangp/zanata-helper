package org.zanata.helper.events;

import lombok.Getter;

/**
 * @author Alex Eng <a href="aeng@redhat.com">aeng@redhat.com</a>
 */
@Getter
public class JobProgressEvent {
    private Long id;
    private int currentStep;
    private int totalSteps;
    private String description;

    public JobProgressEvent(Long id,
            int currentStep, int totalSteps, String description) {
        this.id = id;
        this.currentStep = currentStep;
        this.totalSteps = totalSteps;
        this.description = description;
    }
}
