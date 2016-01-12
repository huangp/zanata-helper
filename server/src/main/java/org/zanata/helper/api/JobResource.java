package org.zanata.helper.api;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.zanata.helper.model.JobStatusType;
import org.zanata.helper.model.JobType;

/**
 * @author Alex Eng <a href="mailto:aeng@redhat.com">aeng@redhat.com</a>
 */
@Path(APIResource.JOB_ROOT)
@Produces("application/json")
public interface JobResource {

    /**
     * @param id - work identifier
     * @param type - {@link JobType}
     */
    @Path("/status")
    @GET
    public Response getJobStatus(
        @QueryParam(value = "id") @DefaultValue("") String id,
        @QueryParam(value = "type") @DefaultValue("")
        JobType type);

    /**
     * @param id - work identifier
     * @param type - {@link JobType}
     */
    @Path("/cancel")
    @POST
    public Response cancelRunningJob(
        @QueryParam(value = "id") @DefaultValue("") String id,
        @QueryParam(value = "type") @DefaultValue("") JobType type);

    /**
     * @param id - work identifier, empty for all job
     * @param type - required if id is present. {@link JobType}
     * @param status - {@link JobStatusType},  empty for all status
     */
    @GET
    Response getJob(
        @QueryParam(value = "id") @DefaultValue("") String id,
        @QueryParam(value = "type") @DefaultValue("") JobType type,
        @QueryParam(value = "status") @DefaultValue("") JobStatusType status);
}
