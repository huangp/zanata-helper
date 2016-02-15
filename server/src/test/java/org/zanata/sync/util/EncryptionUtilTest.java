package org.zanata.sync.util;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

public class EncryptionUtilTest {


    private EncryptionUtil encryptionUtil;

    @Before
    public void setUp() throws Exception {
        byte[] bytes = "hahahah".getBytes("UTF-8");
        encryptionUtil = new EncryptionUtil(bytes);
    }

    @Test
    public void setValueIsEncrypted() throws Exception {
        String encrypt = encryptionUtil.encrypt("plain value");

        Assertions.assertThat(encrypt).isNotEqualTo(
                "plain value");

        Assertions.assertThat(encryptionUtil.decrypt(encrypt)).isEqualTo(
                "plain value");
    }

}
