package org.zanata.helper.Service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * @author Alex Eng <a href="mailto:aeng@redhat.com">aeng@redhat.com</a>
 */
@Service
public class SchedulerServiceImpl implements
    ApplicationListener<ContextRefreshedEvent> {
    private static final Logger logger = LoggerFactory.getLogger(SchedulerServiceImpl.class);

    @Autowired
    private AppConfiguration appConfiguration;

    public void onApplicationEvent(
        ContextRefreshedEvent contextRefreshedEvent) {
        logger.info("=====================================================");
        logger.info("=====================================================");
        logger.info("================Zanata Helper starts=================");
        logger.info(appConfiguration.getBuildVersion() + ":" + appConfiguration.getBuildInfo());
        logger.info("Initialising data from server and start scheduler");
        logger.info("=====================================================");
        logger.info("=====================================================");


        //TODO: database connection, thread count, scheduler, queue, event
    }
}
