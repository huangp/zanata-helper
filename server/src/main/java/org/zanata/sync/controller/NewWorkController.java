package org.zanata.sync.controller;

import javax.enterprise.context.ConversationScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.collections.map.HashedMap;
import org.apache.oltu.oauth2.common.OAuth;
import org.apache.oltu.oauth2.common.token.OAuthToken;
import org.apache.oltu.oauth2.common.utils.OAuthUtils;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.zanata.common.EntityStatus;
import org.zanata.rest.dto.Account;
import org.zanata.rest.dto.Project;
import org.zanata.rest.dto.ProjectIteration;
import org.zanata.sync.api.WorkResource;
import org.zanata.sync.i18n.Messages;
import org.zanata.sync.security.SecurityTokens;
import org.zanata.sync.security.ZanataAuthorized;
import org.zanata.sync.service.PluginsService;
import org.zanata.sync.util.ZanataRestClient;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.google.common.collect.Maps;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.config.DefaultClientConfig;

import static javax.faces.application.FacesMessage.SEVERITY_INFO;

/**
 * @author Alex Eng <a href="mailto:aeng@redhat.com">aeng@redhat.com</a>
 */
@Slf4j
@Named("newWorkController")
@ConversationScoped
public class NewWorkController extends HasFormController {

    @Inject
    private PluginsService pluginsServiceImpl;

    @Inject
    private WorkResource workResourceImpl;

    @Inject
    private Messages msg;

    @Inject
    private SecurityTokens securityTokens;

    @Inject
    private ZanataRestClient zanataRestClient;

    @ZanataAuthorized
    public void check() {
       // we do security check in preRenderView to reduce nasty exception trace in log
    }

    public String onSubmit() throws IOException {
        Response response = workResourceImpl.createWork(form);
        setErrors((Map<String, String>) response.getEntity());
        if (!errors.isEmpty()) {
            log.info("has errors: {}", errors);
            return null;
        }
        FacesContext facesContext = FacesContext.getCurrentInstance();
        FacesMessage message = new FacesMessage(SEVERITY_INFO,
            msg.get("jsf.newWork.created.message"), "");
        facesContext.addMessage(null, message);
        facesContext.getExternalContext().redirect("/home.jsf");
        return "";
    }

    @Override
    public SyncWorkForm getForm() {
        if(form == null) {
            form = new SyncWorkForm();
            // TODO pahuang this is hard coded
            form.getTransServerPluginConfig()
                    .put("username", securityTokens.getZanataUsername());
            form.getTransServerPluginConfig()
                    .put("apiKey", securityTokens.getZanataApiKey());

        }
        return form;
    }

    @Override
    protected Messages getMessage() {
        return msg;
    }

    @Override
    protected PluginsService getPluginService() {
        return pluginsServiceImpl;
    }
}
