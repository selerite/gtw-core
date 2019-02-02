package io.gtw.platform.core.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class JsonUtils {
    private static ObjectMapper objectMapper;
    private static Logger logger = LoggerFactory.getLogger(JsonUtils.class);

    static {
        objectMapper = new ObjectMapper();
    }

    public static <T> T strToObject(String jsonStr, TypeReference<T> valueTypeRef) throws IOException {
        try {
            return objectMapper.readValue(jsonStr, valueTypeRef);
        } catch (IOException e) {
            logger.error(e.getMessage());
            throw e;
        }
    }

    public static <T> T strToObject(String jsonStr, Class<T> clazz) throws IOException {
        try {
            return objectMapper.readValue(jsonStr, clazz);
        } catch (IOException e) {
            logger.error(e.getMessage());
            throw e;
        }
    }
}
