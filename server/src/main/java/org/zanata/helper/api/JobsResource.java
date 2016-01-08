package org.zanata.helper.api;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zanata.helper.service.SchedulerService;

/**
 * @author Alex Eng <a href="mailto:aeng@redhat.com">aeng@redhat.com</a>
 */

@RequestScoped
@Path(APIResource.JOBS_ROOT)
@Produces("application/json")
public class JobsResource {
    private static final Logger log =
            LoggerFactory.getLogger(JobsResource.class);

    public final static String RUNNING_JOBS_URL = "/running";

    @Inject
    private SchedulerService schedulerServiceImpl;

    @Path(RUNNING_JOBS_URL)
    @GET
    public Response getRunningJobs() {
        try {
            return Response.ok(schedulerServiceImpl.getRunningJobs()).build();
        } catch (SchedulerException e) {
            log.error("fail getting running jobs", e);
            return Response.serverError().build();
        }
    }
}
