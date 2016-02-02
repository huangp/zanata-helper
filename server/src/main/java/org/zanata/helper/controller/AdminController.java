package org.zanata.helper.controller;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import org.apache.commons.lang.StringUtils;
import org.hibernate.validator.constraints.NotEmpty;
import org.zanata.helper.component.AppConfiguration;
import org.zanata.helper.i18n.Messages;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import static javax.faces.application.FacesMessage.SEVERITY_INFO;

/**
 * @author Alex Eng <a href="mailto:aeng@redhat.com">aeng@redhat.com</a>
 */
@Named("adminController")
@ViewScoped
@Slf4j
public class AdminController implements Serializable {

    @Inject
    private AppConfiguration appConfiguration;

    @Inject
    private Messages msg;

    @Getter
    @Setter
    @NotEmpty
    private String storageDir;

    @Getter
    @Setter
    private boolean deleteJobDir;

    @Getter
    @Setter
    private String fieldsNeedEncryption;

    @Getter
    @Setter
    private String logbackConfigFile;

    private Map<String, String> errors = new HashMap<>();

    @PostConstruct
    public void init() {
        storageDir = appConfiguration.getStorageDir();
        deleteJobDir = appConfiguration.isDeleteJobDir();
        fieldsNeedEncryption =
            StringUtils.join(appConfiguration.getFieldsNeedEncryption(), ',');
        logbackConfigFile = appConfiguration.getLogbackConfigurationFile().getAbsolutePath();
    }

    public String saveChanges() {
        validate();
        appConfiguration.updateSettingsAndSave(storageDir, deleteJobDir, ImmutableList
            .copyOf(Splitter.on(",").omitEmptyStrings().trimResults()
                .split(fieldsNeedEncryption)), new File(logbackConfigFile));

        FacesMessage message = new FacesMessage(SEVERITY_INFO,
                msg.get("jsf.admin.settings.saved.message"), "");
        FacesContext.getCurrentInstance().addMessage(null, message);
        return "/admin/settings.jsf";
    }

    private void validate() {
        // TODO validate fields
    }

    public boolean hasError(String fieldName) {
        return errors.containsKey(fieldName);
    }

    public String getErrorMessage(String fieldName) {
        return errors.get(fieldName);
    }
}
