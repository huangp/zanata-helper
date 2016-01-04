package org.zanata.helper.component;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.enterprise.context.RequestScoped;

@RequestScoped
public class MessageResource {

    private final ResourceBundle messageSource;

    public MessageResource() {
        Locale locale = Locale.getDefault();
        messageSource = ResourceBundle.getBundle("messages", locale);
    }

    public String getMessage(String messageKey, Object... args) {
        String template = messageSource.getString(messageKey);
        return MessageFormat.format(template, args);
    }

    public String getMessage(String messageKey) {
        return messageSource.getString(messageKey);
    }
}
