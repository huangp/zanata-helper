package org.zanata.helper.action;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.core.Response;

import org.zanata.helper.api.impl.WorkResourceImpl;
import org.zanata.helper.model.SyncWorkConfig;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Alex Eng <a href="mailto:aeng@redhat.com">aeng@redhat.com</a>
 */
@Named("workAction")
@RequestScoped
@Slf4j
public class WorkAction {

    @Inject
    private WorkResourceImpl workResource;

    @Getter
    @Setter
    private String id;

    private SyncWorkConfig syncWorkConfig;

    @PostConstruct
    public void init() {
        getSyncWorkConfig();
    }

    public SyncWorkConfig getSyncWorkConfig() {
        if(syncWorkConfig == null) {
            Response response = workResource.getWork(id, "");
            syncWorkConfig = (SyncWorkConfig)response.getEntity();
        }
        return syncWorkConfig;
    }
}
