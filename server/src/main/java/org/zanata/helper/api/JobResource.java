package org.zanata.helper.api;

import org.apache.commons.lang.StringUtils;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zanata.helper.common.model.Field;
import org.zanata.helper.common.plugin.Plugin;
import org.zanata.helper.common.plugin.RepoExecutor;
import org.zanata.helper.common.plugin.TranslationServerExecutor;
import org.zanata.helper.action.SyncWorkForm;
import org.zanata.helper.common.plugin.Validator;
import org.zanata.helper.exception.JobNotFoundException;
import org.zanata.helper.i18n.Messages;
import org.zanata.helper.model.SyncWorkConfig;
import org.zanata.helper.model.SyncWorkConfigBuilder;
import org.zanata.helper.model.JobConfig;
import org.zanata.helper.service.PluginsService;
import org.zanata.helper.service.SchedulerService;
import org.zanata.helper.validation.SyncWorkFormValidator;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import com.google.common.annotations.VisibleForTesting;

/**
 * @author Alex Eng <a href="mailto:aeng@redhat.com">aeng@redhat.com</a>
 */

@RequestScoped
@Path(APIResource.JOB_ROOT)
@Produces("application/json")
public class JobResource {
    private static final Logger log =
            LoggerFactory.getLogger(JobResource.class);
    public final static String STATUS_URL = "/status";
    public final static String CANCEL_URL = "/cancel";

    @Inject
    private SchedulerService schedulerServiceImpl;

    @Inject
    private PluginsService pluginsServiceImpl;

    @Inject
    private Messages msg;

    @Inject
    private SyncWorkFormValidator formValidator;

    @Path(STATUS_URL)
    @GET
    public Response getJobStatus(
            @QueryParam(value = "id") @DefaultValue("") String id) {
        try {
            if(StringUtils.isEmpty(id)) {
                return Response.status(
                        Response.Status.NOT_FOUND).build();
            }
            return Response.ok(schedulerServiceImpl.getSyncToRepoJobLastStatus(new Long(id))).build();
        } catch (SchedulerException e) {
            log.error("get job status error", e);
            return Response.serverError().build();
        } catch (JobNotFoundException e) {
            log.warn("get job status not found", e);
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @Path(CANCEL_URL)
    @POST
    public Response cancelRunningJob(
        @QueryParam(value = "id") @DefaultValue("") String id,
        @QueryParam(value = "type") @DefaultValue("") String type) {
        try {
            if(StringUtils.isEmpty(id)) {
                return Response.status(
                        Response.Status.NOT_FOUND).build();
            }
            schedulerServiceImpl.cancelRunningJob(new Long(id),
                    JobConfig.Type.valueOf(type));
            return Response.ok().build();
        } catch (SchedulerException e) {
            log.error("cancel error", e);
            return Response.serverError().build();
        } catch (JobNotFoundException e) {
            log.warn("cancel job not found", e);
            return Response.status(
                    Response.Status.NOT_FOUND).build();
        }
    }

    @POST
    public Response createJob(SyncWorkForm form) {
        Map<String, String> errors = formValidator.validateJobForm(form);
        if (!errors.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity(errors).build();
        }

        SyncWorkConfig syncWorkConfig = new SyncWorkConfigBuilder(form).build();
        try {
            schedulerServiceImpl.persistAndScheduleJob(syncWorkConfig);
        }
        catch (SchedulerException e) {
            log.error("Error trying to schedule job", e.getMessage());
            errors.put("error", e.getMessage());
            return Response.serverError().entity(errors).build();
        }
        // TODO create URI
        return Response.created(URI.create("")).entity(errors).build();
    }
}
