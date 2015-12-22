package org.zanata.helper.component;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * @author Alex Eng <a href="mailto:aeng@redhat.com">aeng@redhat.com</a>
 *
 */
@Component
public class AppConfiguration
{
   @Getter
   @Value("${build.version}")
   private String buildVersion;
   
   @Getter
   @Value("${build.info}")
   private String buildInfo;

   @Getter
   @Value("${store.directory}")
   private String storageDirectory;


}
