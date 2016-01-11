package org.zanata.helper.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.zanata.helper.model.JobType;
import com.google.common.base.MoreObjects;

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

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("jobType", jobType)
                .add("currentStep", currentStep)
                .add("totalSteps", totalSteps)
                .add("description", description)
                .toString();
    }
}
