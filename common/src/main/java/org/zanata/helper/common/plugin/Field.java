package org.zanata.helper.common.plugin;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Alex Eng <a href="aeng@redhat.com">aeng@redhat.com</a>
 */
@Getter
public class Field {
    private String key;
    private String label;

    @Setter
    private String value;

    public Field(String key, String label) {
        this.key = key;
        this.label = label;
    }
}
