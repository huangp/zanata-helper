package org.zanata.helper.api.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.ArrayStack;
import org.apache.commons.lang.StringUtils;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zanata.helper.api.APIResource;
import org.zanata.helper.api.JobResource;
import org.zanata.helper.exception.JobNotFoundException;
import org.zanata.helper.model.JobStatusType;
import org.zanata.helper.model.JobSummary;
import org.zanata.helper.model.JobType;
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
public class JobResourceImpl implements JobResource {
    private static final Logger log =
            LoggerFactory.getLogger(JobResourceImpl.class);

    @Inject
    private SchedulerService schedulerServiceImpl;

    @Override
    public Response getJobLastStatus(
        @QueryParam(value = "id") @DefaultValue("") String id,
        @QueryParam(value = "type") @DefaultValue("")
        JobType type) {
        try {
            if(StringUtils.isEmpty(id) || type == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            return Response.ok(schedulerServiceImpl
                .getJobLastStatus(new Long(id), type)).build();
        } catch (SchedulerException e) {
            log.error("get job status error", e);
            return Response.serverError().build();
        } catch (JobNotFoundException e) {
            log.warn("get job status not found", e);
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @Override
    public Response cancelRunningJob(
        @QueryParam(value = "id") @DefaultValue("") String id,
        @QueryParam(value = "type") @DefaultValue("") JobType type) {
        try {
            if(StringUtils.isEmpty(id)) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            schedulerServiceImpl.cancelRunningJob(new Long(id), type);
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

    @Override
    public Response triggerJob(@DefaultValue("") String id,
            @DefaultValue("") JobType type) {
        try {
            if (StringUtils.isEmpty(id)) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            schedulerServiceImpl.startJob(new Long(id), type);
            return Response.ok().build();
        } catch (SchedulerException e) {
            log.error("trigger job error", e);
            return Response.serverError().build();
        } catch (JobNotFoundException e) {
            log.warn("job not found", e);
            return Response.status(
                    Response.Status.NOT_FOUND).build();
        }
    }

    @Override
    public Response getJob(
        @QueryParam(value = "id") @DefaultValue("") String id,
        @QueryParam(value = "type") @DefaultValue("") JobType type,
        @QueryParam(value = "status") @DefaultValue("") JobStatusType status) {
        try {
            List<JobSummary> jobs = schedulerServiceImpl.getJobs();
            if(status == null && StringUtils.isEmpty(id) && type == null) {
                return Response.ok(jobs).build();
            } else {
                List<JobSummary> filteredList = new ArrayList<>();

                boolean filterByKey = !StringUtils.isEmpty(id) && type != null;
                boolean filterByStatus = status != null;

                for(JobSummary summary: jobs) {
                    if (filterByKey && filterByStatus) {
                        JobKey key = type.toJobKey(new Long(id));
                        if (summary.getKey().equals(key.toString())
                                && status.equals(summary.getJobStatus())) {
                            filteredList.add(summary);
                            continue;
                        }
                    } else if(filterByKey) {
                        JobKey key = type.toJobKey(new Long(id));
                        if (summary.getKey().equals(key.toString())) {
                            filteredList.add(summary);
                            continue;
                        }
                    } else if(filterByStatus) {
                        if (status.equals(summary.getJobStatus())) {
                            filteredList.add(summary);
                            continue;
                        }
                    }
                }
                return Response.ok(filteredList).build();
            }
        } catch (SchedulerException e) {
            log.error("fail getting running jobs", e);
            return Response.serverError().build();
        }
    }
}
