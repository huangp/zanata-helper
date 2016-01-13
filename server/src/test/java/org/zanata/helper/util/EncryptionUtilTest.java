package org.zanata.helper.util;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

public class EncryptionUtilTest {


    private EncryptionUtil encryptionUtil;

    @Before
    public void setUp() throws Exception {
        byte[] bytes = "this is a secret key".getBytes("UTF-8");
        encryptionUtil = new EncryptionUtil(bytes);
    }

    @Test
    public void setValueIsEncrypted() throws Exception {
        String encrypt = encryptionUtil.encrypt("plain value");

        Assertions.assertThat(encrypt).isNotEqualTo(
                "plain value");

        Assertions.assertThat(encryptionUtil.decryptValue(encrypt)).isEqualTo(
                "plain value");
    }

}
