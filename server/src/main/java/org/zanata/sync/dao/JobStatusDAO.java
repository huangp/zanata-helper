package org.zanata.sync.dao;

import java.sql.Connection;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zanata.sync.model.JobStatus;
import org.zanata.sync.model.JobStatusList;
import org.zanata.sync.model.JobType;
import org.zanata.sync.model.SyncWorkConfig;

import static org.zanata.sync.db.public_.tables.JobStatusTable.JOB_STATUS_TABLE;

/**
 * @author Patrick Huang <a href="mailto:pahuang@redhat.com">pahuang@redhat.com</a>
 */
@ApplicationScoped
public class JobStatusDAO {
    private static final Logger log =
            LoggerFactory.getLogger(JobStatusDAO.class);

    @Inject
    private Connection connection;

    public JobStatusList getJobStatusList(SyncWorkConfig config, JobType type) {
        DSLContext dslContext = DSL.using(connection, SQLDialect.H2);

        Result<Record> statusRecords = dslContext.select()
                .from(JOB_STATUS_TABLE)
                .where(JOB_STATUS_TABLE.WORKID.eq(config.getId())
                        .and(JOB_STATUS_TABLE.JOBTYPE
                                .eq(type.name())))
                .orderBy(JOB_STATUS_TABLE.ID.desc())
                .fetch();

        List<JobStatus> jobStatusList = statusRecords.stream()
                .map(record -> new JobStatus(record.getValue(
                        JOB_STATUS_TABLE.JOBSTATUSTYPE),
                        toDate(record.getValue(JOB_STATUS_TABLE.STARTTIME)),
                        toDate(record.getValue(JOB_STATUS_TABLE.ENDTIME)),
                        toDate(record
                                .getValue(JOB_STATUS_TABLE.NEXTSTARTTIME))))
                .collect(Collectors.toList());
        return new JobStatusList(jobStatusList);
    }

    private static Date toDate(Timestamp timestamp) {
        return new Date(timestamp.getTime());
    }

    public void saveJobStatus(SyncWorkConfig config, JobType type, JobStatus jobStatus) {
        DSLContext dslContext = DSL.using(connection, SQLDialect.H2);


        dslContext.insertInto(JOB_STATUS_TABLE, JOB_STATUS_TABLE.WORKID,
                JOB_STATUS_TABLE.JOBTYPE, JOB_STATUS_TABLE.JOBSTATUSTYPE,
                JOB_STATUS_TABLE.STARTTIME, JOB_STATUS_TABLE.ENDTIME,
                JOB_STATUS_TABLE.NEXTSTARTTIME)
                .values(config.getId(), type.name(), jobStatus.getStatus(),
                        toTimestamp(jobStatus.getLastStartTime()),
                        toTimestamp(jobStatus.getLastEndTime()),
                        toTimestamp(jobStatus.getNextStartTime()))
                .execute();

        log.info("JobStatus saved." + config.getName() + ":" + type);
    }

    private static Timestamp toTimestamp(Date date) {
        return new Timestamp(date.getTime());
    }
}
