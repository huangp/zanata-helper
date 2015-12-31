package org.zanata.helper.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

/**
 * @author Alex Eng <a href="aeng@redhat.com">aeng@redhat.com</a>
 */
@AllArgsConstructor
@Getter
public class JobProgress implements Serializable {
    private int currentStep;
    private int totalSteps;
    private String description;
}
