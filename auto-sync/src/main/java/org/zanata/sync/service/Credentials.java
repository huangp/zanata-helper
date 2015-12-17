package org.zanata.sync.service;

/**
 * @author Patrick Huang <a href="mailto:pahuang@redhat.com">pahuang@redhat.com</a>
 */
public interface Credentials<P> {
    String getUsername();
    P getSecret();
}
