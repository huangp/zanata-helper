package org.zanata.helper.service.impl;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @author Alex Eng(aeng)  loones1595@gmail.com
 *
 */
@Service
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
