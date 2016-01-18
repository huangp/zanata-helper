package org.zanata.helper.model;

import java.io.Serializable;
import java.util.List;

import lombok.Getter;

/**
 * @author Alex Eng <a href="mailto:aeng@redhat.com">aeng@redhat.com</a>
 */
@Getter
public class SystemSettings implements Serializable {
    private String storageDir;
    private boolean deleteJobDir;
    private List<String> fieldsNeedEncryption;
}
