package org.zanata.sync.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Alex Eng <a href="aeng@redhat.com">aeng@redhat.com</a>
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class JobProgress implements Serializable {

    private double completePercent;
    private String description;
    private JobStatusType status;
}
