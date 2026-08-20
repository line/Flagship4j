/*
 * Copyright 2026 LY Corporation
 *
 * LY Corporation licenses this file to you under the Apache License,
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
package com.linecorp.flagship4j.javaflagr.configs;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URI;
import java.time.Duration;

import org.junit.jupiter.api.Test;

class OpenFlagrConfigTest {

    private static final String ENDPOINT = "http://localhost:18000";

    @Test
    void builderAcceptsStringEndpoint() {
        final OpenFlagrConfig config = OpenFlagrConfig.builder()
                                                      .endpoint(ENDPOINT)
                                                      .build();

        assertEquals(URI.create(ENDPOINT), config.getEndpoint());
    }

    @Test
    void builderAcceptsUriEndpoint() {
        final URI endpoint = URI.create(ENDPOINT);

        final OpenFlagrConfig config = OpenFlagrConfig.builder()
                                                      .endpoint(endpoint)
                                                      .build();

        assertEquals(endpoint, config.getEndpoint());
    }

    @Test
    void builderAcceptsMillisecondTimeouts() {
        final OpenFlagrConfig config = OpenFlagrConfig.builder()
                                                      .endpoint(ENDPOINT)
                                                      .connectionTimeoutMs(100)
                                                      .readTimeoutMs(200)
                                                      .writeTimeoutMs(300)
                                                      .callTimeoutMs(400)
                                                      .build();

        assertEquals(Duration.ofMillis(100), config.getConnectionTimeout());
        assertEquals(Duration.ofMillis(200), config.getReadTimeout());
        assertEquals(Duration.ofMillis(300), config.getWriteTimeout());
        assertEquals(Duration.ofMillis(400), config.getCallTimeout());
    }

    @Test
    void builderAcceptsSecondTimeouts() {
        final OpenFlagrConfig config = OpenFlagrConfig.builder()
                                                      .endpoint(ENDPOINT)
                                                      .connectionTimeoutSeconds(1)
                                                      .readTimeoutSeconds(2)
                                                      .writeTimeoutSeconds(3)
                                                      .callTimeoutSeconds(4)
                                                      .build();

        assertEquals(Duration.ofSeconds(1), config.getConnectionTimeout());
        assertEquals(Duration.ofSeconds(2), config.getReadTimeout());
        assertEquals(Duration.ofSeconds(3), config.getWriteTimeout());
        assertEquals(Duration.ofSeconds(4), config.getCallTimeout());
    }

    @Test
    void builderAcceptsDurationTimeouts() {
        final OpenFlagrConfig config = OpenFlagrConfig.builder()
                                                      .endpoint(ENDPOINT)
                                                      .connectionTimeout(Duration.ofSeconds(5))
                                                      .readTimeout(Duration.ofSeconds(6))
                                                      .writeTimeout(Duration.ofSeconds(7))
                                                      .callTimeout(Duration.ofSeconds(8))
                                                      .build();

        assertEquals(Duration.ofSeconds(5), config.getConnectionTimeout());
        assertEquals(Duration.ofSeconds(6), config.getReadTimeout());
        assertEquals(Duration.ofSeconds(7), config.getWriteTimeout());
        assertEquals(Duration.ofSeconds(8), config.getCallTimeout());
    }

    @Test
    void noArgsConstructorLeavesEverythingUnset() {
        final OpenFlagrConfig config = new OpenFlagrConfig();

        assertNull(config.getEndpoint());
        assertNull(config.getConnectionTimeout());
        assertNull(config.getReadTimeout());
        assertNull(config.getWriteTimeout());
        assertNull(config.getCallTimeout());
    }

    @Test
    void allArgsConstructorAndSettersAssignEveryField() {
        final OpenFlagrConfig config = new OpenFlagrConfig(
                URI.create(ENDPOINT),
                Duration.ofSeconds(1),
                Duration.ofSeconds(2),
                Duration.ofSeconds(3),
                Duration.ofSeconds(4));

        assertEquals(Duration.ofSeconds(1), config.getConnectionTimeout());

        config.setEndpoint(URI.create("http://localhost:18001"));
        config.setConnectionTimeout(Duration.ofSeconds(9));
        config.setReadTimeout(Duration.ofSeconds(9));
        config.setWriteTimeout(Duration.ofSeconds(9));
        config.setCallTimeout(Duration.ofSeconds(9));

        assertEquals(URI.create("http://localhost:18001"), config.getEndpoint());
        assertEquals(Duration.ofSeconds(9), config.getConnectionTimeout());
        assertEquals(Duration.ofSeconds(9), config.getReadTimeout());
        assertEquals(Duration.ofSeconds(9), config.getWriteTimeout());
        assertEquals(Duration.ofSeconds(9), config.getCallTimeout());
    }

    @Test
    void equalsHashCodeAndToStringAreValueBased() {
        final OpenFlagrConfig first = OpenFlagrConfig.builder().endpoint(ENDPOINT).callTimeoutSeconds(3).build();
        final OpenFlagrConfig second = OpenFlagrConfig.builder().endpoint(ENDPOINT).callTimeoutSeconds(3).build();
        final OpenFlagrConfig other = OpenFlagrConfig.builder().endpoint(ENDPOINT).callTimeoutSeconds(4).build();

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
        assertNotEquals(first, other);
        assertTrue(first.toString().contains(ENDPOINT));
    }

    @Test
    void builderRejectsMalformedStringEndpoint() {
        assertThrows(IllegalArgumentException.class, () -> OpenFlagrConfig.builder().endpoint("http://a b"));
    }
}
