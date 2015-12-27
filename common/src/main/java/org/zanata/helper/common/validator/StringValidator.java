package org.zanata.helper.common.validator;

import lombok.AllArgsConstructor;
import org.zanata.helper.common.plugin.Validator;

/**
 * @author Alex Eng <a href="aeng@redhat.com">aeng@redhat.com</a>
 */
@AllArgsConstructor
public class StringValidator implements Validator {
    private Integer minLength;
    private Integer maxLength;
    //Allow empty string
    private Boolean notEmpty;

    @Override
    public String validate(String value) {
        if (notEmpty != null) {
            if (value == null || value.length() <= 0) {
                return "must not empty";
            }
        }
        if (minLength != null) {
            if (value == null || value.length() < minLength) {
                return "must have at least " + minLength + " character";
            }
        }
        if (maxLength != null) {
            if (value == null || value.length() > maxLength) {
                return "must not more than " + minLength + " character";
            }
        }
        return null;
    }
}
