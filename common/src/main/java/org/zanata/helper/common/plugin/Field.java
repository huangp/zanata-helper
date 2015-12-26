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

    private String placeholder;
    private String tooltip;

    public Field(String key, String label, String placeholder, String tooltip) {
        this.key = key;
        this.label = label;
        this.placeholder = placeholder;
        this.tooltip = tooltip;
    }
}
