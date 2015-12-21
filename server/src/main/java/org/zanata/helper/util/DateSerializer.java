package org.zanata.helper.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Alex Eng <a href="aeng@redhat.com">aeng@redhat.com</a>
 */
public class DateSerializer extends JsonSerializer<Date> {
    private final static String FORMAT = "yyyy-MM-dd HH:mm:ss";
    private final static SimpleDateFormat FORMATTER = new SimpleDateFormat(
        FORMAT);

    @Override
    public void serialize(Date value, JsonGenerator generator,
        SerializerProvider provider)
        throws IOException, JsonProcessingException {
        if(value != null) {
            generator.writeString(FORMATTER.format(value));
        }
    }
}
