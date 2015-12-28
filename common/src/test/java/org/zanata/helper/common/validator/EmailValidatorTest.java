package org.zanata.helper.common.validator;

import org.assertj.core.api.Assertions;
import org.junit.Test;

/**
 * @author Alex Eng <a href="aeng@redhat.com">aeng@redhat.com</a>
 */
public class EmailValidatorTest {

    private EmailValidator emailValidator = new EmailValidator();

    @Test
    public void validEmailTest() {
        String validEmail = "test@domain.com";
        Assertions.assertThat(emailValidator.validate(validEmail)).isNull();
    }

    @Test
    public void invalidEmailTest() {
        String invalidEmail = "testdomain.com";
        Assertions.assertThat(emailValidator.validate(invalidEmail)).isNotNull();
    }
}
