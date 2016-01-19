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
    private final String key;
    private final String label;

    @Setter
    private String value;

    private final FieldType type;

    private final String placeholder;
    private final String tooltip;
    private final Validator validator;

    public Field(String key, String label, String placeholder, String tooltip,
            FieldType type) {
        this(key, label, placeholder, tooltip, null, type);
    }

    public Field(String key, String label, String placeholder, String tooltip,
        Validator validator, FieldType type) {
        this.key = key;
        this.label = label;
        this.placeholder = placeholder;
        this.tooltip = tooltip;
        this.validator = validator;
        this.type = type;
    }
}
