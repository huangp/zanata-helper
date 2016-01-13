/*
 * Copyright 2015, Red Hat, Inc. and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.zanata.helper.util;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author Patrick Huang <a
 *         href="mailto:pahuang@redhat.com">pahuang@redhat.com</a>
 */
public class EncryptionUtil {
    private static Cipher cipher = makeCipher();

    private static Cipher makeCipher() {
        try {
            return Cipher.getInstance("AES/ECB/PKCS5Padding");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new IllegalStateException("can not create cipher", e);
        }
    }

    // TODO make key configurable
    private byte[] keyBytes = new byte[] { 0x00, 0x01, 0x02, 0x03, 0x04, 0x05,
            0x06, 0x07, 0x08, 0x09,
            0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f, 0x10, 0x11, 0x12, 0x13, 0x14,
            0x15, 0x16, 0x17 };

    public EncryptionUtil(byte[] keyBytes) {
        this.keyBytes = keyBytes;
    }
    // java only allow 128bit (16 chars) in key by default
    private SecretKeySpec key = new SecretKeySpec(keyBytes, 0, 16, "AES");

    public String encrypt(String input) {
        // encryption pass
        try {
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encrypted = cipher.doFinal(input.getBytes("UTF-8"));
            return Base64.getEncoder().encodeToString((encrypted));
        } catch (InvalidKeyException | BadPaddingException
                | IllegalBlockSizeException | UnsupportedEncodingException e) {
            throw new IllegalStateException("failed to encrypt input", e);
        }
    }



    public String decryptValue(String cipherText) {
        // decryption pass
        try {
            cipher.init(Cipher.DECRYPT_MODE, key);
            return new String(
                    cipher.doFinal(Base64.getDecoder().decode(cipherText)));
        } catch (InvalidKeyException | BadPaddingException
                | IllegalBlockSizeException e) {
            throw new IllegalStateException("failed to decrypt value", e);
        }

    }
}
