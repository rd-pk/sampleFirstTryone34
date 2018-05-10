package com.prokarma.rep;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.jackson.Jackson;

public class PersonRepHelper {

    private static final ObjectMapper MAPPER = Jackson.newObjectMapper();

    public <T> String serializesToJSON(final T person) throws Exception {

        return MAPPER.writeValueAsString(person);
    }


    public <T> T readJson(String json, java.lang.Class<T> valueType) {
        try {
            return MAPPER.readValue(json, valueType);
        } catch (Exception e) {
            return null;
        }
    }
}

