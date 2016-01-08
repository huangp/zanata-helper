package org.zanata.helper.api;

import org.apache.commons.lang.StringUtils;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zanata.helper.exception.JobNotFoundException;
import org.zanata.helper.model.JobConfig;
import org.zanata.helper.service.SchedulerService;

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
@Path(APIResource.JOB_ROOT)
@Produces("application/json")
public class JobResource {
    private static final Logger log =
            LoggerFactory.getLogger(JobResource.class);
    public final static String STATUS_URL = "/status";
    public final static String CANCEL_URL = "/cancel";

    @Inject
    private SchedulerService schedulerServiceImpl;

    @Path(STATUS_URL)
    @GET
    public Response getJobStatus(
        @QueryParam(value = "id") @DefaultValue("") String id,
        @QueryParam(value = "type") @DefaultValue("")
        String type) {
        try {
            if(StringUtils.isEmpty(id) || StringUtils.isEmpty(type)) {
                return Response.status(
                        Response.Status.NOT_FOUND).build();
            }
            return Response.ok(schedulerServiceImpl
                .getJobLastStatus(new Long(id), JobConfig.Type.valueOf(type)))
                .build();
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
}
