package org.zanata.helper.api;

import java.util.List;

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
import org.zanata.helper.model.JobInfo;
import org.zanata.helper.model.JobStatus;
import org.zanata.helper.service.SchedulerService;
import org.zanata.helper.service.impl.AppConfiguration;

/**
 * @author Alex Eng <a href="mailto:aeng@redhat.com">aeng@redhat.com</a>
 */

@RestController
public class JobController {

    @Autowired
    private SchedulerService schedulerServiceImpl;

    @RequestMapping(value = "/api/jobs/status", method = RequestMethod.GET,
            produces = "application/json; charset=UTF-8")
    @ResponseBody
    public ResponseEntity<JobStatus> getJobStatus(
            @RequestParam(value = "sha", defaultValue = "") String sha) {
        try {
            return new ResponseEntity<JobStatus>(
                    schedulerServiceImpl.getStatus(sha), HttpStatus.OK);
        } catch (SchedulerException e) {
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (TaskNotFoundException e) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/api/jobs/running", method = RequestMethod.GET,
            produces = "application/json; charset=UTF-8")
    @ResponseBody
    public ResponseEntity<List<JobInfo>> getRunningJobs() {
        try {
            return new ResponseEntity<List<JobInfo>>(
                    schedulerServiceImpl.getRunningJob(), HttpStatus.OK);
        } catch (SchedulerException e) {
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/api/jobs", method = RequestMethod.GET,
        produces = "application/json; charset=UTF-8")
    @ResponseBody
    public ResponseEntity<List<JobInfo>> getAllJobs() {
        try {
            return new ResponseEntity<List<JobInfo>>(
                schedulerServiceImpl.getAllJobs(), HttpStatus.OK);
        } catch (SchedulerException e) {
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
