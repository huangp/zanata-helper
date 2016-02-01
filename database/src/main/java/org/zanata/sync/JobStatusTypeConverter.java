package org.zanata.sync;

import org.jooq.impl.EnumConverter;
import org.zanata.helper.model.JobStatusType;

/**
 * @author Patrick Huang <a href="mailto:pahuang@redhat.com">pahuang@redhat.com</a>
 */
public class JobStatusTypeConverter extends EnumConverter<String, JobStatusType> {
    public JobStatusTypeConverter() {
        super(String.class, JobStatusType.class);
    }
}
