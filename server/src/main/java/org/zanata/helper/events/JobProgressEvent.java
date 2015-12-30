package org.zanata.helper.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * @author Alex Eng <a href="aeng@redhat.com">aeng@redhat.com</a>
 */
@Getter
public class JobProgressEvent extends ApplicationEvent {
    private Long id;
    private int currentStep;
    private int totalSteps;
    private String description;

    public JobProgressEvent(Object source, Long id,
        int currentStep, int totalSteps, String description) {
        super(source);
        this.id = id;
        this.currentStep = currentStep;
        this.totalSteps = totalSteps;
        this.description = description;
    }
}
