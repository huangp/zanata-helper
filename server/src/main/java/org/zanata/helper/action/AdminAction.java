package org.zanata.helper.action;

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
import org.zanata.helper.repository.SystemSettingsRepository;

import static javax.faces.application.FacesMessage.SEVERITY_INFO;

/**
 * @author Alex Eng <a href="mailto:aeng@redhat.com">aeng@redhat.com</a>
 */
@Named("adminAction")
@ViewScoped
@Slf4j
public class AdminAction implements Serializable {

    @Inject
    private AppConfiguration appConfiguration;

    @Inject
    private SystemSettingsRepository systemSettingsRepository;

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

    private Map<String, String> errors = new HashMap<>();

    @PostConstruct
    public void init() {
        storageDir = appConfiguration.getStorageDirectory();
        deleteJobDir = appConfiguration.isDeleteJobDir();
        fieldsNeedEncryption =
            StringUtils.join(appConfiguration.getFieldsNeedEncryption(), ',');
    }

    public String saveChanges() {
        appConfiguration.updateSettings(storageDir, deleteJobDir, ImmutableList
            .copyOf(Splitter.on(",").omitEmptyStrings().trimResults()
                .split(fieldsNeedEncryption)));

        systemSettingsRepository.persist(appConfiguration.getSystemSettings());
        FacesMessage message = new FacesMessage(SEVERITY_INFO,
                msg.get("jsf.admin.settings.saved.message"), "");
        FacesContext.getCurrentInstance().addMessage(null, message);
        return "/admin/settings.jsf";
    }

    public boolean hasError(String fieldName) {
        return errors.containsKey(fieldName);
    }

    public String getErrorMessage(String fieldName) {
        return errors.get(fieldName);
    }
}
