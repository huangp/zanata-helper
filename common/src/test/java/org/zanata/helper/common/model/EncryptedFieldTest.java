package org.zanata.helper.common.model;

import java.nio.charset.Charset;
import java.security.Security;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class EncryptedFieldTest {

    private EncryptedField encryptedField;

    @Before
    public void setUp() throws Exception {
        encryptedField =
                new EncryptedField("key", "label", "placeholder", "tooltip");

    }

    @Test
    public void setValueIsEncrypted() {
        encryptedField.setValue("plain value");

        Assertions.assertThat(encryptedField.getValue()).isNotEqualTo(
                "plain value");

        Assertions.assertThat(encryptedField.decryptValue()).isEqualTo(
                "plain value");
    }
}
