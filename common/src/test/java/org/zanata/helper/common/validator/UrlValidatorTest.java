package org.zanata.helper.common.validator;

import org.assertj.core.api.Assertions;
import org.junit.Test;

/**
 * @author Alex Eng <a href="aeng@redhat.com">aeng@redhat.com</a>
 */
public class UrlValidatorTest {
    private UrlValidator urlValidator = new UrlValidator();

    @Test
    public void validUrlTest() {
        String validUrl = "http://zanata.org";
        Assertions.assertThat(urlValidator.validate(validUrl)).isNull();
    }

    @Test
    public void invalidUrlTest() {
        String invalidUrl = "zanata.org";
        Assertions.assertThat(urlValidator.validate(invalidUrl)).isNotNull();
    }
}
