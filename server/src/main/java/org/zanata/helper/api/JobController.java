package org.zanata.helper.api;

import org.apache.commons.lang.StringUtils;
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

import java.util.List;

/**
 * @author Alex Eng <a href="mailto:aeng@redhat.com">aeng@redhat.com</a>
 */

@RestController
@RequestMapping(value = APIController.API_ROOT + APIController.JOB_ROOT)
public class JobController extends APIController {

    public final static String STATUS_URL = "/status";
    public final static String CANCEL_URL = "/cancel";

    @Autowired
    private SchedulerService schedulerServiceImpl;

    @RequestMapping(value = STATUS_URL, method = RequestMethod.GET,
            produces = "application/json; charset=UTF-8")
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

    @RequestMapping(value = CANCEL_URL, method = RequestMethod.POST,
        produces = "application/json; charset=UTF-8")
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


}
