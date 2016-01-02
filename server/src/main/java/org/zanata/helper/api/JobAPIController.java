package org.zanata.helper.api;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
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

import java.util.HashMap;
import java.util.Map;

/**
 * @author Alex Eng <a href="mailto:aeng@redhat.com">aeng@redhat.com</a>
 */

@RestController
@RequestMapping(value = APIController.API_ROOT + APIController.JOB_ROOT)
@Slf4j
public class JobAPIController extends APIController {

    public final static String STATUS_URL = "/status";
    public final static String CANCEL_URL = "/cancel";

    @Autowired
    private SchedulerService schedulerServiceImpl;

    @Autowired
    private PluginsService pluginsServiceImpl;

    @Autowired
    private MessageResource messageResource;

    @RequestMapping(value = STATUS_URL, method = RequestMethod.GET, produces = CHARSET_JSON_UTF8)
    @ResponseBody
    public ResponseEntity<JobStatus> getJobStatus(
            @RequestParam(value = "id", defaultValue = "") String id) {
        try {
            if(StringUtils.isEmpty(id)) {
                return new ResponseEntity(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<JobStatus>(
                    schedulerServiceImpl.getLastStatus(new Long(id)),
                    HttpStatus.OK);
        } catch (SchedulerException e) {
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (JobNotFoundException e) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = CANCEL_URL, method = RequestMethod.POST, produces = CHARSET_JSON_UTF8)
    @ResponseBody
    public ResponseEntity<String> cancelRunningJob(
        @RequestParam(value = "id", defaultValue = "") String id) {
        try {
            if(StringUtils.isEmpty(id)) {
                return new ResponseEntity(HttpStatus.NOT_FOUND);
            }
            schedulerServiceImpl.cancelRunningJob(new Long(id));
            return new ResponseEntity<String>(HttpStatus.OK);
        } catch (SchedulerException e) {
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (JobNotFoundException e) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(method = RequestMethod.POST, produces = CHARSET_JSON_UTF8)
    @ResponseBody
    public ResponseEntity<Map<String, String>> createJob(
        @RequestBody JobForm form) {
        Map<String, String> errors = validateJobForm(form);
        if (!errors.isEmpty()) {
            return new ResponseEntity<Map<String, String>>(errors,
                HttpStatus.BAD_REQUEST);
        }

        JobConfig jobConfig = new JobConfigBuilder(form).build();
        try {
            schedulerServiceImpl.persistAndScheduleJob(jobConfig);
        }
        catch (SchedulerException e) {
            log.error("Error trying to schedule job", e.getMessage());
            errors.put("error", e.getMessage());
            return new ResponseEntity<Map<String, String>>(
                HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<Map<String, String>>(HttpStatus.CREATED);
    }

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
