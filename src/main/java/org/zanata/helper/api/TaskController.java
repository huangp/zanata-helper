package org.zanata.helper.api;

import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.zanata.helper.exception.TaskNotFoundException;
import org.zanata.helper.model.JobStatus;
import org.zanata.helper.service.SchedulerService;
import org.zanata.helper.service.impl.AppConfiguration;

/**
 * @author Alex Eng <a href="mailto:aeng@redhat.com">aeng@redhat.com</a>
 */

@RestController
public class TaskController {

    @Autowired
    private SchedulerService schedulerServiceImpl;

    @RequestMapping(value = "/rest/task/status", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<JobStatus> getRequestTaskStatus(
        @RequestParam(value = "key", defaultValue = "") String key) {
        try {
            return new ResponseEntity<JobStatus>(
                schedulerServiceImpl.getStatus(key), HttpStatus.OK);
        } catch (SchedulerException e) {
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (TaskNotFoundException e) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
    }
}
