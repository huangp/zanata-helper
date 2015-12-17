package org.zanata.sync.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.zanata.client.commands.OptionsUtil;
import org.zanata.client.commands.push.PushCommand;
import org.zanata.client.commands.push.PushOptions;
import org.zanata.client.config.LocaleList;
import org.zanata.client.config.LocaleMapping;
import org.zanata.rest.client.ProjectIterationLocalesClient;
import org.zanata.rest.client.RestClientFactory;
import org.zanata.rest.dto.LocaleDetails;
import org.zanata.sync.service.PushService;

/**
 * @author Patrick Huang <a href="mailto:pahuang@redhat.com">pahuang@redhat.com</a>
 */
public class PushServiceImpl implements PushService {

    private final PushOptions pushOptions;

    public PushServiceImpl(PushOptions pushOptions) {
        this.pushOptions = pushOptions;
    }

    public PushOptions getPushOptions() {
        return pushOptions;
    }

    public void push() throws Exception {
        LocaleList localesList = getLocalesFromServer();
        pushOptions.setLocaleMapList(localesList);
        PushCommand pushCommand = new PushCommand(pushOptions);
        pushCommand.run();
    }

    // TODO open up OptionsUtil method to public so that we can reuse it here
    private LocaleList getLocalesFromServer() {
        RestClientFactory restClientFactory =
                OptionsUtil.createClientFactoryWithoutVersionCheck(pushOptions);
        ProjectIterationLocalesClient projectLocalesClient = restClientFactory
                .getProjectLocalesClient(pushOptions.getProj(),
                        pushOptions.getProjectVersion());
        List<LocaleDetails> locales = projectLocalesClient.getLocales();
        LocaleList localesList = new LocaleList();
        for (LocaleDetails details : locales) {
            localesList.add(new LocaleMapping(details.getLocaleId().getId(), details.getAlias()));
        }
        return localesList;
    }
}
