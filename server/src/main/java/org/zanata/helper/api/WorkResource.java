package org.zanata.helper.api;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.zanata.helper.action.SyncWorkForm;

/**
 * @author Alex Eng <a href="mailto:aeng@redhat.com">aeng@redhat.com</a>
 */
@Path(APIResource.WORK_ROOT)
@Produces("application/json")
public interface WorkResource {

    /**
     * @param id - id for work. If empty return list of work
     * @param type - summary for WorkSummary, or empty(default) for SyncWorkConfig
     */
    @GET
    public Response
    getWork(@QueryParam(value = "id") @DefaultValue("") String id,
        @QueryParam(value = "type") @DefaultValue("") String type);

    /**
     * Create work
     * @param form - {@link SyncWorkForm}
     */
    @POST
    @Consumes("application/json")
    public Response createWork(SyncWorkForm form);

    /**
     * Delete work permanently
     *
     * @param id - work id
     */
    @DELETE
    @Consumes("application/json")
    public Response deleteWork(
        @QueryParam(value = "id") @DefaultValue("") String id);

    /**
     * Disable all jobs in work temporarily
     *
     * @param id - work id
     */
    @POST
    @Consumes("application/json")
    @Path("/disable")
    public Response disableWork(
        @QueryParam(value = "id") @DefaultValue("") String id);

    /**
     * Enable all jobs in work if disabled
     *
     * @param id - work id
     */
    @POST
    @Consumes("application/json")
    @Path("/enable")
    public Response enableWork(
        @QueryParam(value = "id") @DefaultValue("") String id);
}
