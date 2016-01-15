package org.zanata.helper.action;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.core.Response;

import org.zanata.helper.api.JobResource;
import org.zanata.helper.api.WorkResource;
import org.zanata.helper.i18n.Messages;
import org.zanata.helper.model.JobStatusType;
import org.zanata.helper.model.JobSummary;
import org.zanata.helper.model.JobType;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.zanata.helper.model.SyncWorkConfig;
import org.zanata.helper.model.SyncWorkConfigBuilder;
import org.zanata.helper.service.PluginsService;

/**
 * @author Alex Eng <a href="mailto:aeng@redhat.com">aeng@redhat.com</a>
 */
@Named("workAction")
@Slf4j
@ViewScoped
public class WorkAction extends HasFormAction {

    @Inject
    private WorkResource workResourceImpl;

    @Inject
    private JobResource jobResource;

    @Inject
    private PluginsService pluginsServiceImpl;

    @Inject
    private SyncWorkConfigBuilder syncWorkConfigBuilderImpl;

    @Inject
    private Messages msg;

    @Getter
    @Setter
    private String id;

    private SyncWorkConfig syncWorkConfig;

    public SyncWorkForm getForm() {
        if(form == null) {
            form = syncWorkConfigBuilderImpl.buildForm(getSyncWorkConfig());
        }
        return form;
    }

    public SyncWorkConfig getSyncWorkConfig() {
        if(syncWorkConfig == null) {
            Response response = workResourceImpl.getWork(id, "");
            syncWorkConfig = (SyncWorkConfig)response.getEntity();
        }
        return syncWorkConfig;
    }

    public void triggerSyncToRepoJob() {
        jobResource.triggerJob(id, JobType.REPO_SYNC);
    }

    public void triggerSyncToServerJob() {
        jobResource.triggerJob(id, JobType.SERVER_SYNC);
    }

    public boolean isSyncToRepoRunning() {
        return isJobRunning(JobType.REPO_SYNC);
    }

    public boolean isSyncToServerRunning() {
        return isJobRunning(JobType.SERVER_SYNC);
    }

    public void deleteWork() throws IOException {
        workResourceImpl.deleteWork(id);
        FacesContext.getCurrentInstance().getExternalContext()
            .redirect("/home.xhtml");
    }

    public void disableWork() {
        workResourceImpl.disableWork(id);
    }

    public void enableWork() {
        workResourceImpl.enableWork(id);
    }

    private boolean isJobRunning(JobType jobType) {
        Response response =
                jobResource.getJob(id, jobType, JobStatusType.RUNNING);
        List<JobSummary> result = (List<JobSummary>) response.getEntity();
        return !result.isEmpty();
    }

    @Override
    protected Messages getMessage() {
        return msg;
    }

    @Override
    protected PluginsService getPluginService() {
        return pluginsServiceImpl;
    }

    @Override
    public String onSubmit() throws IOException {
        Response response = workResourceImpl.updateWork(form);
        errors = (Map<String, String>) response.getEntity();
        if (!errors.isEmpty()) {
            return "/work/home.xhtml";
        }
        FacesContext.getCurrentInstance().getExternalContext()
            .redirect("/home.xhtml");
        return "";
    }
}
