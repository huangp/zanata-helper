package org.zanata.helper.action;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.hibernate.validator.constraints.NotEmpty;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Alex Eng <a href="mailto:aeng@redhat.com">aeng@redhat.com</a>
 */
@Named("adminAction")
@ViewScoped
@Slf4j
public class AdminAction implements Serializable {

    @Getter
    @Setter
    @NotEmpty
    private String storageDir;

    @Getter
    @Setter
    private boolean deleteJobDir;

    @Getter
    @Setter
    private boolean enableEncryption;

    private Map<String, String> errors = new HashMap<>();

    @PostConstruct
    public void init() {
        //populate all fields
    }


    public String saveChanges() {
        validateFields();
        return "/admin/settings.jsf";
    }

    private void validateFields() {

    }

    public boolean hasError(String fieldName) {
        return errors.containsKey(fieldName);
    }

    public String getErrorMessage(String fieldName) {
        return errors.get(fieldName);
    }
}
