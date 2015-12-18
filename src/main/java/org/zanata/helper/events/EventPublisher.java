package org.zanata.helper.events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.stereotype.Component;

@Component
public class EventPublisher
{
   @Autowired
   private SimpleApplicationEventMulticaster simpleApplicationEventMulticaster;

   private static final Logger
       logger = LoggerFactory.getLogger(EventPublisher.class);

   private final SimpleAsyncTaskExecutor
       asyncTaskExecutor = new SimpleAsyncTaskExecutor();

   public void fireEvent(ApplicationEvent event)
   {
      logger.debug("Fire event: " + event.toString());
      simpleApplicationEventMulticaster.setTaskExecutor(asyncTaskExecutor);
      simpleApplicationEventMulticaster.multicastEvent(event);
   }
}
