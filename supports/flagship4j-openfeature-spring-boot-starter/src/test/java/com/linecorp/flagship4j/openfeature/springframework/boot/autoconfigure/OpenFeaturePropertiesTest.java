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
package com.linecorp.flagship4j.openfeature.springframework.boot.autoconfigure;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class OpenFeaturePropertiesTest {

    @Test
    void appliesThirtySecondTimeoutsByDefault() {
        final OpenFeatureProperties properties = new OpenFeatureProperties();

        assertThat(properties.getBaseUrl()).isNull();
        assertThat(properties.getConnectionTimeout()).isEqualTo(30);
        assertThat(properties.getReadTimeout()).isEqualTo(30);
        assertThat(properties.getCallTimeout()).isEqualTo(30);
        assertThat(properties.getWriteTimeout()).isEqualTo(30);
    }

    @Test
    void keepsEveryOverriddenValue() {
        final OpenFeatureProperties properties = new OpenFeatureProperties();
        properties.setBaseUrl("http://localhost:18000");
        properties.setConnectionTimeout(1);
        properties.setReadTimeout(2);
        properties.setCallTimeout(3);
        properties.setWriteTimeout(4);

        assertThat(properties.getBaseUrl()).isEqualTo("http://localhost:18000");
        assertThat(properties.getConnectionTimeout()).isEqualTo(1);
        assertThat(properties.getReadTimeout()).isEqualTo(2);
        assertThat(properties.getCallTimeout()).isEqualTo(3);
        assertThat(properties.getWriteTimeout()).isEqualTo(4);
    }

    @Test
    void isValueBased() {
        final OpenFeatureProperties first = new OpenFeatureProperties();
        first.setBaseUrl("http://localhost:18000");
        final OpenFeatureProperties second = new OpenFeatureProperties();
        second.setBaseUrl("http://localhost:18000");
        final OpenFeatureProperties other = new OpenFeatureProperties();
        other.setBaseUrl("http://localhost:18001");
        final OpenFeatureProperties differentTimeout = new OpenFeatureProperties();
        differentTimeout.setBaseUrl("http://localhost:18000");
        differentTimeout.setCallTimeout(1);

        assertThat(first).isEqualTo(first)
                         .isEqualTo(second)
                         .hasSameHashCodeAs(second)
                         .isNotEqualTo(other)
                         .isNotEqualTo(differentTimeout)
                         .isNotEqualTo(null)
                         .isNotEqualTo("not a properties object");
        assertThat(first.toString()).contains("http://localhost:18000");
    }
}
