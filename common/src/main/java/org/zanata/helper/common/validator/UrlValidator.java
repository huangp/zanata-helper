package org.zanata.helper.common.validator;

import org.zanata.helper.common.Messages;
import org.zanata.helper.common.plugin.Validator;

/**
 * @author Alex Eng <a href="aeng@redhat.com">aeng@redhat.com</a>
 */
public class UrlValidator implements Validator {

    private final org.apache.commons.validator.routines.UrlValidator
        urlValidator = new
        org.apache.commons.validator.routines.UrlValidator(
            org.apache.commons.validator.routines.UrlValidator.ALLOW_LOCAL_URLS);

    @Override
    public String validate(String value) {
        if(value == null || value.length() <= 0) {
            return Messages.getString("validation.string.notEmpty");
        }
        if(urlValidator.isValid(value)) {
           return null;
        }
        return Messages.getString("validation.url.invalid", value);
    }
}
