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
import org.zanata.helper.exception.JobNotFoundException;
import org.zanata.helper.model.JobStatus;
import org.zanata.helper.model.JobSummary;
import org.zanata.helper.service.SchedulerService;

/**
 * @author Alex Eng <a href="mailto:aeng@redhat.com">aeng@redhat.com</a>
 */

@RestController
@RequestMapping(value = APIController.API_ROOT + APIController.JOBS_ROOT)
public class JobsController extends APIController {

    public final static String RUNNING_JOBS_URL = "/running";

    @Autowired
    private SchedulerService schedulerServiceImpl;


    @RequestMapping(method = RequestMethod.GET,
        produces = "application/json; charset=UTF-8")
    @ResponseBody
    public ResponseEntity<List<JobSummary>> getAllJobs() {
        try {
            return new ResponseEntity<List<JobSummary>>(
                schedulerServiceImpl.getAllJobs(), HttpStatus.OK);
        } catch (SchedulerException e) {
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = RUNNING_JOBS_URL, method = RequestMethod.GET,
            produces = "application/json; charset=UTF-8")
    @ResponseBody
    public ResponseEntity<List<JobSummary>> getRunningJobs() {
        try {
            return new ResponseEntity<List<JobSummary>>(
                    schedulerServiceImpl.getRunningJob(), HttpStatus.OK);
        } catch (SchedulerException e) {
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
