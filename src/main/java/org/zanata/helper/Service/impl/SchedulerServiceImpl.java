package org.zanata.helper.Service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * @author Alex Eng <a href="mailto:aeng@redhat.com">aeng@redhat.com</a>
 */
@Component
public class SchedulerServiceImpl implements
    ApplicationListener<ContextRefreshedEvent> {
    private static final Logger logger = LoggerFactory.getLogger(SchedulerServiceImpl.class);

    public void onApplicationEvent(
        ContextRefreshedEvent contextRefreshedEvent) {
        logger.info("=====================================================");
        logger.info("=====================================================");
        logger.info("On startup, load data from server and start scheduler");
        logger.info("=====================================================");
        logger.info("=====================================================");

        //TODO: database connection, thread count, scheduler, queue, event
    }
}
