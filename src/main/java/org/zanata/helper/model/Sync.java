package org.zanata.helper.model;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Alex Eng <a href="mailto:aeng@redhat.com">aeng@redhat.com</a>
 */
public interface Sync extends Serializable {

    Type getType();

    Long getId();

    String getCron();

    Date getLastExecuted();

    String getSourceRepositoryUrl();

    String getZanataVersionUrl();

    static enum Type {
        SYNC_TO_ZANATA,
        SYNC_TO_REPO;
    }
}
