package org.zanata.sync.security;

import java.io.IOException;
import java.io.PrintWriter;
import javax.inject.Inject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.oltu.oauth2.client.response.OAuthAuthzResponse;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Patrick Huang <a href="mailto:pahuang@redhat.com">pahuang@redhat.com</a>
 */
//@WebFilter(filterName = "authorizationCodeFilter", urlPatterns = "/auth/*")
public class AuthorizationCodeFilter implements Filter {
    private static final Logger log =
            LoggerFactory.getLogger(AuthorizationCodeFilter.class);
    @Inject
    private SecurityTokens securityTokens;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {
        if (!(request instanceof HttpServletRequest)) {
            chain.doFilter(request, response);
            return;
        }
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;

        try {
            OAuthAuthzResponse oAuthResponse = OAuthAuthzResponse
                    .oauthCodeAuthzResponse(httpServletRequest);
            String code = oAuthResponse.getCode();
            securityTokens.setAuthorizationCode(code);
            // see org.zanata.sync.controller.ZanataSignIn
            String originalRequest = httpServletRequest.getRequestURI()
                    .replaceFirst("/auth/", "");
            httpServletResponse.sendRedirect(originalRequest);
        } catch (OAuthProblemException e) {
            log.warn("=== problem with OAuth", e);

            httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            httpServletResponse.setContentType("text/html");
            PrintWriter writer = httpServletResponse.getWriter();
            writer.print("<h2>error:</h2>");
            writer.println(e.getError());
            writer.flush();
            writer.close();
        }
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}
