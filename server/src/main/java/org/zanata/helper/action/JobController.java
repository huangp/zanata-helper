package org.zanata.helper.action;

import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.zanata.helper.exception.JobNotFoundException;
import org.zanata.helper.model.SyncWorkConfig;
import org.zanata.helper.service.SchedulerService;

/**
 * @author Alex Eng <a href="aeng@redhat.com">aeng@redhat.com</a>
 */
//@Controller
@Slf4j
public class JobController {
    @Inject
    private SchedulerService schedulerServiceImpl;

//    @RequestMapping(value = "/job", params = {"id"}, method = RequestMethod.GET)
    public String getNewJobPage(/*ModelMap model,
        @RequestParam(value = "id", defaultValue = "") String id*/)
        throws JobNotFoundException {

        String id = ""; // FIXME temp
        if(StringUtils.isEmpty(id)) {
            throw new JobNotFoundException(id);
        }
        SyncWorkConfig syncWorkConfig = schedulerServiceImpl.getJob(new Long(id));
        if(syncWorkConfig == null) {
            throw new JobNotFoundException(id);
        }
//        model.addAttribute("job", jobConfig);
        return "job";
    }
}
