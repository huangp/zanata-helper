package org.zanata.helper.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Alex Eng <a href="mailto:aeng@redhat.com">aeng@redhat.com</a>
 */
@Getter
@AllArgsConstructor
public class SystemSettings implements Serializable {
    /**
     * Must have read write access
     * i.e /tmp/zanataHelperRoot
     */
    private String storageDir;
    private boolean deleteJobDir;
    private List<String> fieldsNeedEncryption = new ArrayList<>();
}
