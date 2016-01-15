package org.zanata.helper.api.impl;

import org.apache.commons.lang.StringUtils;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zanata.helper.action.SyncWorkForm;
import org.zanata.helper.api.WorkResource;
import org.zanata.helper.exception.WorkNotFoundException;
import org.zanata.helper.model.SyncWorkConfig;
import org.zanata.helper.model.SyncWorkConfigBuilder;
import org.zanata.helper.model.WorkSummary;
import org.zanata.helper.service.SchedulerService;
import org.zanata.helper.service.WorkService;
import org.zanata.helper.validation.SyncWorkFormValidator;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.Map;

/**
 * @author Alex Eng <a href="mailto:aeng@redhat.com">aeng@redhat.com</a>
 */

@RequestScoped
public class WorkResourceImpl implements WorkResource {
    private static final Logger log =
            LoggerFactory.getLogger(WorkResourceImpl.class);

    @Inject
    private SchedulerService schedulerServiceImpl;

    @Inject
    private WorkService workServiceImpl;

    @Inject
    private SyncWorkFormValidator formValidator;

    @Inject
    private SyncWorkConfigBuilder syncWorkConfigBuilder;

    @Override
    public Response
            getWork(@QueryParam(value = "id") @DefaultValue("") String id,
                    @QueryParam(value = "type") @DefaultValue("") String type) {
        if (StringUtils.isEmpty(id)) {
            return getAllWork(type);
        } else {
            try {
                if(!type.equals("summary")) {
                    return Response.ok(schedulerServiceImpl.getWork(id)).build();
                } else {
                    return Response.ok(schedulerServiceImpl.getWorkSummary(id)).build();
                }
            } catch (WorkNotFoundException e) {
                log.error("fail getting all jobs", e);
                return Response.status(Response.Status.NOT_FOUND).build();
            }
        }
    }

    @Override
    public Response createWork(SyncWorkForm form) {
        Map<String, String> errors = formValidator.validateJobForm(form);
        if (!errors.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity(errors).build();
        }
        SyncWorkConfig syncWorkConfig = syncWorkConfigBuilder.buildObject(form);
        try {
            workServiceImpl.updateOrPersist(syncWorkConfig);
            schedulerServiceImpl.scheduleWork(syncWorkConfig);
        } catch (SchedulerException e) {
            log.error("Error trying to schedule job", e);
            errors.put("error", e.getMessage());
            return Response.serverError().entity(errors).build();
        }
        // TODO create URI
        return Response.created(URI.create("")).entity(errors).build();
    }

    @Override
    public Response updateWork(SyncWorkForm form) {
        if(form.getId() == null) {
            return createWork(form);
        }
        Map<String, String> errors = formValidator.validateJobForm(form);
        if (!errors.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity(errors).build();
        }
        SyncWorkConfig syncWorkConfig = syncWorkConfigBuilder.buildObject(form);

        try {
            workServiceImpl.updateOrPersist(syncWorkConfig);
            schedulerServiceImpl.rescheduleWork(syncWorkConfig);
        } catch (SchedulerException e) {
            log.error("Error rescheduling work", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(errors).build();
        }
        // TODO create URI
        return Response.created(URI.create("")).entity(errors).build();
    }

    @Override
    public Response deleteWork(String id) {
        try {
            workServiceImpl.deleteWork(new Long(id));
        } catch (WorkNotFoundException e) {
            log.error("No work found", e);
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.status(Response.Status.OK).build();
    }

    @Override
    public Response disableWork(String id) {
        WorkSummary summary;
        try {
            summary = workServiceImpl.disableWork(new Long(id));
        } catch (WorkNotFoundException e) {
            log.error("No work found", e);
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.status(Response.Status.OK).entity(summary).build();
    }

    @Override
    public Response enableWork(String id) {
        WorkSummary summary;
        try {
            summary = workServiceImpl.enableWork(new Long(id));
        } catch (WorkNotFoundException e) {
            log.error("No work found", e);
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.status(Response.Status.OK).entity(summary).build();
    }

    private Response getAllWork(String type) {
        try {
            if(!type.equals("summary")) {
                return Response.ok(schedulerServiceImpl.getAllWork()).build();
            } else {
                return Response.ok(schedulerServiceImpl.getAllWorkSummary()).build();
            }
        } catch (SchedulerException e) {
            log.error("fail getting all jobs", e);
            return Response.serverError().build();
        }
    }
}
