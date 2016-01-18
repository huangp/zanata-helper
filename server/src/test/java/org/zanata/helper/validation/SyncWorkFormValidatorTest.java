package org.zanata.helper.validation;

import java.util.Map;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.zanata.helper.controller.SyncWorkForm;
import org.zanata.helper.service.PluginsService;

public class SyncWorkFormValidatorTest {

    private SyncWorkFormValidator validator;

    @Mock
    private PluginsService pluginsService;
    private SyncWorkForm form;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        validator = new SyncWorkFormValidator();
        form = new SyncWorkForm();

        validator.pluginsService = pluginsService;
    }

    @Test
    public void testFormValidation() {
        form.setName("a");
        Map<String, String> errors = validator.validateJobForm(form);

        Assertions.assertThat(errors).isNotEmpty();
        Assertions.assertThat(errors.get("name"))
                .isEqualTo("size must be between 5 and 100");
    }
}
