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
package com.linecorp.flagship4j.javaflagr.config.properties;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class Flagship4jToggleFlagrPropertiesTest {

    @Test
    void appliesTheDocumentedDefaults() {
        final Flagship4jToggleFlagrProperties properties = new Flagship4jToggleFlagrProperties();

        assertThat(properties.getBaseUrl()).isNull();
        assertThat(properties.getConnectionTimeout()).isEqualTo(30);
        assertThat(properties.getReadTimeout()).isEqualTo(30);
        assertThat(properties.getDefaultTimeout()).isEqualTo(30);
        assertThat(properties.getRetry()).isEqualTo(2);
    }

    @Test
    void keepsEveryOverriddenValue() {
        final Flagship4jToggleFlagrProperties properties = new Flagship4jToggleFlagrProperties();
        properties.setBaseUrl("http://localhost:18000");
        properties.setConnectionTimeout(1);
        properties.setReadTimeout(2);
        properties.setDefaultTimeout(3);
        properties.setRetry(4);

        assertThat(properties.getBaseUrl()).isEqualTo("http://localhost:18000");
        assertThat(properties.getConnectionTimeout()).isEqualTo(1);
        assertThat(properties.getReadTimeout()).isEqualTo(2);
        assertThat(properties.getDefaultTimeout()).isEqualTo(3);
        assertThat(properties.getRetry()).isEqualTo(4);
    }

    @Test
    void isValueBased() {
        final Flagship4jToggleFlagrProperties first = propertiesWithBaseUrl("http://localhost:18000");
        final Flagship4jToggleFlagrProperties second = propertiesWithBaseUrl("http://localhost:18000");
        final Flagship4jToggleFlagrProperties other = propertiesWithBaseUrl("http://localhost:18001");

        assertThat(first).isEqualTo(first)
                         .isEqualTo(second)
                         .hasSameHashCodeAs(second)
                         .isNotEqualTo(other)
                         .isNotEqualTo(null)
                         .isNotEqualTo("not a properties object");
        assertThat(first.toString()).contains("http://localhost:18000");
    }

    @Test
    void differsOnEveryField() {
        final Flagship4jToggleFlagrProperties reference = propertiesWithBaseUrl("http://localhost:18000");

        final Flagship4jToggleFlagrProperties differentConnectionTimeout =
                propertiesWithBaseUrl("http://localhost:18000");
        differentConnectionTimeout.setConnectionTimeout(1);

        final Flagship4jToggleFlagrProperties differentReadTimeout = propertiesWithBaseUrl("http://localhost:18000");
        differentReadTimeout.setReadTimeout(1);

        final Flagship4jToggleFlagrProperties differentDefaultTimeout =
                propertiesWithBaseUrl("http://localhost:18000");
        differentDefaultTimeout.setDefaultTimeout(1);

        final Flagship4jToggleFlagrProperties differentRetry = propertiesWithBaseUrl("http://localhost:18000");
        differentRetry.setRetry(1);

        final Flagship4jToggleFlagrProperties withoutBaseUrl = new Flagship4jToggleFlagrProperties();

        assertThat(reference).isNotEqualTo(differentConnectionTimeout)
                             .isNotEqualTo(differentReadTimeout)
                             .isNotEqualTo(differentDefaultTimeout)
                             .isNotEqualTo(differentRetry)
                             .isNotEqualTo(withoutBaseUrl);
        assertThat(withoutBaseUrl).isNotEqualTo(reference);
    }

    private static Flagship4jToggleFlagrProperties propertiesWithBaseUrl(String baseUrl) {
        final Flagship4jToggleFlagrProperties properties = new Flagship4jToggleFlagrProperties();
        properties.setBaseUrl(baseUrl);
        return properties;
    }
}
