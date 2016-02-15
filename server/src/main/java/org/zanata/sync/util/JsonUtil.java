package org.zanata.sync.util;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Alex Eng <a href="mailto:aeng@redhat.com">aeng@redhat.com</a>
 */
public class JsonUtil {
    
    public final static String toJson(Object object) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            return object.getClass().getName() + "@"
                    + Integer.toHexString(object.hashCode());
        }
    }

    public final static <T> T fromJson(String json, Class<T> clazz) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return (T) mapper.readValue(json, clazz);
        } catch (IOException e) {
            return null;
        }
    }
}
