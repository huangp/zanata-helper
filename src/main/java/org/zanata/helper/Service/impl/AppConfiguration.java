package org.zanata.helper.Service.impl;

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
   @Value("${build.date}")
   private String buildDate;
}
