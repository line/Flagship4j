/*
 * Copyright 2024 LINE Corporation
 *
 * LINE Corporation licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.linecorp.flagship4j.javaflagr.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.util.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder(toBuilder = true)
public class Context {

    public static final String DEFAULT_HEADER_NAME = "LINESPM-CONTEXT";

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private String serviceId;

    private List<String> roles = Collections.emptyList();

    private Map<String, Object> properties = new HashMap<>();

    public static Context anonymous() {
        return builder().serviceId("anonymous").build();
    }

    public static Context fromContextString(String contextString) throws IOException {
        final String jsonString = new String(Base64.getDecoder().decode(contextString));
        return OBJECT_MAPPER.readValue(jsonString, Context.class);
    }

    public String toContextString() throws IOException {
        final String jsonString = OBJECT_MAPPER.writeValueAsString(this);
        return Base64.getEncoder().encodeToString(jsonString.getBytes());
    }

    public <T> Optional<T> getProperty(String key) {
        T result = (T) properties.get(key);
        return Optional.ofNullable(result);
    }

    public <T> T getPropertyOrDefault(String key, T defaultValue) {
        return (T) getProperty(key).orElse(defaultValue);
    }

}
