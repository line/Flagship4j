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
package com.linecorp.flagship4j.javaflagr.utils;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;

import org.jeasy.random.EasyRandomParameters;
import org.junit.jupiter.api.Test;

import com.linecorp.flagship4j.javaflagr.model.PostEvaluationResponse;

class RandomUtilsTest {

    @Test
    void generateObjectPopulatesEveryField() {
        final PostEvaluationResponse result = RandomUtils.generateObject(PostEvaluationResponse.class);

        assertThat(result).isNotNull();
        assertThat(result.getFlagID()).isNotNull();
        assertThat(result.getFlagKey()).isNotNull();
        assertThat(result.getVariantKey()).isNotNull();
        assertThat(result.getEvalContext()).isNotNull();
    }

    @Test
    void generateObjectReturnsADifferentValueOnEveryCall() {
        final PostEvaluationResponse first = RandomUtils.generateObject(PostEvaluationResponse.class);
        final PostEvaluationResponse second = RandomUtils.generateObject(PostEvaluationResponse.class);

        assertThat(first.getFlagKey()).isNotEqualTo(second.getFlagKey());
    }

    @Test
    void generateObjectHonoursACustomParametersTemplate() {
        final EasyRandomParameters parameters = new EasyRandomParameters()
                .charset(StandardCharsets.UTF_8)
                .collectionSizeRange(2, 2)
                .stringLengthRange(5, 5)
                .ignoreRandomizationErrors(true);

        final PostEvaluationResponse result = RandomUtils.generateObject(PostEvaluationResponse.class, parameters);

        assertThat(result.getFlagKey()).hasSize(5);
    }

    @Test
    void generateAlphanumericStringReturnsAStringOfTheRequestedLength() {
        final String result = RandomUtils.generateAlphanumericString(12);

        assertThat(result).hasSize(12).containsPattern("^[A-Za-z0-9]+$");
    }

    @Test
    void generateNumericStringReturnsDigitsOnly() {
        final String result = RandomUtils.generateNumericString(8);

        assertThat(result).hasSize(8).containsPattern("^[0-9]+$");
    }

    @Test
    void generateEmptyStringsWhenTheRequestedLengthIsZero() {
        assertThat(RandomUtils.generateAlphanumericString(0)).isEmpty();
        assertThat(RandomUtils.generateNumericString(0)).isEmpty();
    }
}
