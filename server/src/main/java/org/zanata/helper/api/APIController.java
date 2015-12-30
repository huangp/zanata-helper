package org.zanata.helper.api;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * @author Alex Eng <a href="mailto:aeng@redhat.com">aeng@redhat.com</a>
 */
public abstract class APIController {
    public final static String API_ROOT = "/api";

    public final static String JOBS_ROOT = "/jobs";
    public final static String JOB_ROOT = "/job";

    public static String getAPIUrl() {
        return getBaseUrl() + API_ROOT;
    }

    public static String getBaseUrl() {
        HttpServletRequest request =
            ((ServletRequestAttributes) RequestContextHolder
                .currentRequestAttributes()).getRequest();
        return String.format("%s://%s:%d", request.getScheme(),
            request.getServerName(), request.getServerPort());
    }
}
