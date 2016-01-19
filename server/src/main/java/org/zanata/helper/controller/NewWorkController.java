package org.zanata.helper.controller;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.core.Response;

import org.zanata.helper.api.WorkResource;
import org.zanata.helper.i18n.Messages;
import org.zanata.helper.service.PluginsService;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Map;

import static javax.faces.application.FacesMessage.SEVERITY_INFO;

/**
 * @author Alex Eng <a href="mailto:aeng@redhat.com">aeng@redhat.com</a>
 */
@ViewScoped
@Slf4j
@Named("newWorkController")
public class NewWorkController extends HasFormController {

    @Inject
    private PluginsService pluginsServiceImpl;

    @Inject
    private WorkResource workResourceImpl;

    @Inject
    private Messages msg;

    public String onSubmit() throws IOException {
        Response response = workResourceImpl.createWork(form);
        errors = (Map<String, String>) response.getEntity();
        if (!errors.isEmpty()) {
            return "/work/new.jsf";
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