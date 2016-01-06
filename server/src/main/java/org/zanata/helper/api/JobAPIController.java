package org.zanata.helper.api;

import org.apache.commons.lang.StringUtils;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zanata.helper.common.model.Field;
import org.zanata.helper.common.plugin.Plugin;
import org.zanata.helper.common.plugin.RepoExecutor;
import org.zanata.helper.common.plugin.TranslationServerExecutor;
import org.zanata.helper.common.plugin.Validator;
import org.zanata.helper.component.MessageResource;
import org.zanata.helper.controller.JobForm;
import org.zanata.helper.exception.JobNotFoundException;
import org.zanata.helper.model.JobConfig;
import org.zanata.helper.model.JobConfigBuilder;
import org.zanata.helper.model.JobStatus;
import org.zanata.helper.service.PluginsService;
import org.zanata.helper.service.SchedulerService;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

/**
 * @author Alex Eng <a href="mailto:aeng@redhat.com">aeng@redhat.com</a>
 */

@RequestScoped
@Path(APIController.JOB_ROOT)
@Produces("application/json")
public class JobAPIController {
    private static final Logger log =
            LoggerFactory.getLogger(JobAPIController.class);
    public final static String STATUS_URL = "/status";
    public final static String CANCEL_URL = "/cancel";

    @Inject
    private SchedulerService schedulerServiceImpl;

    @Inject
    private PluginsService pluginsServiceImpl;

    @Inject
    private MessageResource messageResource;

    @Path(STATUS_URL)
    @GET
    public Response getJobStatus(
            @QueryParam(value = "id") @DefaultValue("") String id) {
        try {
            if(StringUtils.isEmpty(id)) {
                return Response.status(
                        Response.Status.NOT_FOUND).build();
            }
            return Response.ok(schedulerServiceImpl.getLastStatus(new Long(id))).build();
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
        @QueryParam(value = "id") @DefaultValue("") String id) {
        try {
            if(StringUtils.isEmpty(id)) {
                return Response.status(
                        Response.Status.NOT_FOUND).build();
            }
            schedulerServiceImpl.cancelRunningJob(new Long(id));
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
    public Response createJob(JobForm form) {
        Map<String, String> errors = validateJobForm(form);
        if (!errors.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity(errors).build();
        }

        JobConfig jobConfig = new JobConfigBuilder(form).build();
        try {
            schedulerServiceImpl.persistAndScheduleJob(jobConfig);
        }
        catch (SchedulerException e) {
            log.error("Error trying to schedule job", e.getMessage());
            errors.put("error", e.getMessage());
            return Response.serverError().build();
        }
        // TODO create URI
        return Response.created(URI.create("")).build();
    }

    // TODO use bean validation
    private Map<String, String> validateJobForm(JobForm form) {
        Map<String, String> errors = new HashMap<>();

        if (StringUtils.length(form.getName()) < form.getNAME_MIN() ||
            StringUtils.length(form.getName()) > form.getNAME_MAX()) {
            errors.put("name", messageResource
                .getMessage("jsf.validation.constraints.Size.message",
                    form.getNAME_MIN(),
                    form.getNAME_MAX()));
        }
        if (StringUtils.length(form.getDescription()) >
            form.getDESCRIPTION_MAX()) {
            errors.put("description", messageResource
                .getMessage("jsf.validation.constraints.Size.max",
                    form.getDESCRIPTION_MAX()));
        }
        if (!StringUtils.isEmpty(form.getCron()) &&
            StringUtils.length(form.getCron()) >
                form.getCRON_MAX()) {
            errors.put("cron", messageResource
                .getMessage("jsf.validation.constraints.Size.max",
                    form.getCRON_MAX()));
        }

        if (StringUtils.length(form.getSourceRepoExecutorName()) <
            form.getSOURCE_REPO_NAME_MIN() ||
            StringUtils.length(form.getSourceRepoExecutorName()) >
                form.getSOURCE_REPO_NAME_MAX()) {
            errors.put("sourceRepoExecutorName", messageResource
                .getMessage("jsf.validation.constraints.Size.message",
                    form.getSOURCE_REPO_NAME_MIN(),
                    form.getSOURCE_REPO_NAME_MAX()));
        }

        if (StringUtils.length(form.getTranslationServerExecutorName()) <
            form.getTRAN_SERVER_NAME_MIN() ||
            StringUtils.length(form.getTranslationServerExecutorName()) >
                form.getTRAN_SERVER_NAME_MAX()) {
            errors.put("translationServerExecutorName", messageResource
                .getMessage("jsf.validation.constraints.Size.message",
                    form.getTRAN_SERVER_NAME_MIN(),
                    form.getTRAN_SERVER_NAME_MAX()));
        }

        errors.putAll(validateRepoFields(form.getSourceRepoConfig(),
            form.getSourceRepoExecutorName()));
        errors.putAll(validateTransFields(form.getTransServerConfig(),
            form.getTranslationServerExecutorName()));

        return errors;
    }

    private Map<String, String> validateRepoFields(
        Map<String, String> config, String executorClass) {
        RepoExecutor executor = pluginsServiceImpl.getNewSourceRepoPlugin(
            executorClass);
        if(executor == null) {
            return new HashMap<>();
        }
        return validateFields(config, executor, JobForm.repoSettingsPrefix);
    }

    private Map<String, String> validateTransFields(
        Map<String, String> config, String executorClass) {
        TranslationServerExecutor executor =
            pluginsServiceImpl.getNewTransServerPlugin(executorClass);
        if(executor == null) {
            return new HashMap<>();
        }
        return validateFields(config, executor, JobForm.transSettingsPrefix);
    }

    private Map<String, String> validateFields(Map<String, String> config,
        Plugin executor, String prefix) {
        Map<String, String> errors = new HashMap<>();

        for (Map.Entry<String, String> entry : config.entrySet()) {
            Field field = executor.getFields().get(entry.getKey());
            if (field != null && field.getValidator() != null) {
                Validator validator = field.getValidator();
                String message = validator.validate(entry.getValue());
                if (!StringUtils.isEmpty(message)) {
                    errors.put(prefix + entry.getKey(), message);
                }
            }
        }
        return errors;
    }
}
