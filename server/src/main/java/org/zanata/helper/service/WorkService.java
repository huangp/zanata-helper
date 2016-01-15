package org.zanata.helper.service;

import org.zanata.helper.exception.WorkNotFoundException;
import org.zanata.helper.model.SyncWorkConfig;
import org.zanata.helper.model.WorkSummary;

/**
 * @author Alex Eng <a href="aeng@redhat.com">aeng@redhat.com</a>
 */
public interface WorkService {
    void deleteWork(Long id) throws WorkNotFoundException;

    WorkSummary disableWork(Long id) throws WorkNotFoundException;

    WorkSummary enableWork(Long id) throws WorkNotFoundException;

    void updateOrPersist(SyncWorkConfig syncWorkConfig);
}
