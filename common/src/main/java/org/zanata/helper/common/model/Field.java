package org.zanata.helper.common.model;

import lombok.Getter;
import lombok.Setter;
import org.zanata.helper.common.plugin.Validator;

import java.io.Serializable;

/**
 * @author Alex Eng <a href="aeng@redhat.com">aeng@redhat.com</a>
 */
@Getter
public class Field implements Serializable {
    private String key;
    private String label;

    @Setter
    private String value;

    private String placeholder;
    private String tooltip;
    private Validator validator;

    public Field(String key, String label, String placeholder, String tooltip) {
        this(key, label, placeholder, tooltip, null);
    }

    public Field(String key, String label, String placeholder, String tooltip,
        Validator validator) {
        this.key = key;
        this.label = label;
        this.placeholder = placeholder;
        this.tooltip = tooltip;
        this.validator = validator;
    }
}
