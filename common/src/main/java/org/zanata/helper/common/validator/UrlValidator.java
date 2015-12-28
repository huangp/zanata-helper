package org.zanata.helper.common.validator;

import org.zanata.helper.common.Messages;
import org.zanata.helper.common.plugin.Validator;

/**
 * @author Alex Eng <a href="aeng@redhat.com">aeng@redhat.com</a>
 */
public class UrlValidator implements Validator {

    private final org.apache.commons.validator.routines.UrlValidator
        urlValidator =
        org.apache.commons.validator.routines.UrlValidator.getInstance();

    @Override
    public String validate(String value) {
        if(urlValidator.isValid(value)) {
           return null;
        }
        return Messages.getString("validation.url.invalid", value);
    }
}
