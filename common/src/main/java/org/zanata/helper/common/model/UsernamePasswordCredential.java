package org.zanata.helper.common.model;

/**
 * @author Patrick Huang <a href="mailto:pahuang@redhat.com">pahuang@redhat.com</a>
 */
public class UsernamePasswordCredential implements Credentials<String> {

    private final EncryptedField username;
    private final EncryptedField apiKey;

    public UsernamePasswordCredential(EncryptedField username,
            EncryptedField apiKey) {
        this.username = username;
        this.apiKey = apiKey;
    }

    public String getUsername() {
        return username.decryptValue();
    }

    public String getSecret() {
        return apiKey.decryptValue();
    }
}
