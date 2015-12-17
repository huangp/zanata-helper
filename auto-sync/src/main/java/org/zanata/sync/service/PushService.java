package org.zanata.sync.service;

import org.zanata.client.commands.push.PushOptions;

public interface PushService {
    PushOptions getPushOptions();

    void push() throws Exception;
}
