package org.zanata.helper.api;

import java.util.List;

import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.zanata.helper.exception.TaskNotFoundException;
import org.zanata.helper.model.JobStatus;
import org.zanata.helper.model.JobInfo;
import org.zanata.helper.service.SchedulerService;

/**
 * @author Alex Eng <a href="mailto:aeng@redhat.com">aeng@redhat.com</a>
 */

@RestController
public class JobController extends APIController {

    public final static String STATUS_URL = API_ROOT + "/jobs/status";
    public final static String RUNNING_JOBS_URL = API_ROOT + "/jobs/running";
    public final static String ALL_JOBS_URL = API_ROOT + "/jobs";

    @Autowired
    private SchedulerService schedulerServiceImpl;

    @RequestMapping(value = STATUS_URL, method = RequestMethod.GET,
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

    @RequestMapping(value = RUNNING_JOBS_URL, method = RequestMethod.GET,
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

    @RequestMapping(value = ALL_JOBS_URL, method = RequestMethod.GET,
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
