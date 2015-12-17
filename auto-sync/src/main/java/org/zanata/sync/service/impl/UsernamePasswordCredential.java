package org.zanata.sync.service.impl;

import java.util.Base64;

import org.zanata.sync.service.Credentials;

/**
 * @author Patrick Huang <a href="mailto:pahuang@redhat.com">pahuang@redhat.com</a>
 */
public class UsernamePasswordCredential implements Credentials<String> {
    private final String username;
    private final String password;

    public UsernamePasswordCredential(String username, String password) {
        this.username = username;
        this.password = password;;
    }

    public String getUsername() {
        return username;
    }

    public String getSecret() {
        return password;
    }
}
