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
package com.linecorp.flagship4j.javaflagr.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.linecorp.flagship4j.javaflagr.client.FlagrEvalClient;
import com.linecorp.flagship4j.javaflagr.exception.FlagrException;
import com.linecorp.flagship4j.javaflagr.model.EvaluationContext;
import com.linecorp.flagship4j.javaflagr.model.PostEvaluationResponse;
import com.linecorp.flagship4j.javaflagr.service.impl.FlagrEvalServiceImpl;

/**
 * Covers the failure handling of {@link FlagrEvalServiceImpl} that the fixture based
 * {@code FlagrEvalServiceTest} cannot reach, namely a response without a flag id and an unexpected
 * client failure.
 */
class FlagrEvalServiceImplTest {

    private static final String FLAG_KEY = "exist_feature_flag";

    private FlagrEvalClient flagrEvalClient;

    private FlagrEvalService flagrEvalService;

    @BeforeEach
    void setUp() {
        flagrEvalClient = mock(FlagrEvalClient.class);
        flagrEvalService = new FlagrEvalServiceImpl(flagrEvalClient);
    }

    @Test
    void isFeatureFlagOnIsFalseWhenTheResponseCarriesNoFlagId() {
        givenResponse(new PostEvaluationResponse());

        assertThat(flagrEvalService.isFeatureFlagOn(FLAG_KEY)).isFalse();
    }

    @Test
    void isFeatureFlagOnIsFalseWhenTheClientFailsWithAFlagrException() {
        when(flagrEvalClient.evaluate(any(EvaluationContext.class)))
                .thenThrow(new FlagrException("Connection Fail."));

        assertThat(flagrEvalService.isFeatureFlagOn(FLAG_KEY)).isFalse();
    }

    @Test
    void isFeatureFlagOnIsFalseWhenTheClientFailsUnexpectedly() {
        when(flagrEvalClient.evaluate(any(EvaluationContext.class)))
                .thenThrow(new IllegalStateException("unexpected"));

        assertThat(flagrEvalService.isFeatureFlagOn(FLAG_KEY)).isFalse();
    }

    @Test
    void isFeatureFlagOnByVariantKeyIsFalseWhenTheClientFailsUnexpectedly() {
        when(flagrEvalClient.evaluate(any(EvaluationContext.class)))
                .thenThrow(new IllegalStateException("unexpected"));

        assertThat(flagrEvalService.isFeatureFlagOn(FLAG_KEY, "on")).isFalse();
        assertThat(flagrEvalService.isFeatureFlagOn(FLAG_KEY, "on", Collections.emptyMap())).isFalse();
    }

    @Test
    void getVariantKeyIsEmptyWhenTheResponseCarriesNoFlagId() {
        givenResponse(new PostEvaluationResponse());

        assertThat(flagrEvalService.getVariantKey(FLAG_KEY)).isEmpty();
    }

    @Test
    void getVariantKeyIsEmptyWhenTheClientFailsUnexpectedly() {
        when(flagrEvalClient.evaluate(any(EvaluationContext.class)))
                .thenThrow(new IllegalStateException("unexpected"));

        assertThat(flagrEvalService.getVariantKey(FLAG_KEY)).isEmpty();
    }

    @Test
    void isFeatureFlagOnIsTrueForAnOnVariant() {
        givenResponse(responseWith("on"));

        assertThat(flagrEvalService.isFeatureFlagOn(FLAG_KEY)).isTrue();
        assertThat(flagrEvalService.isFeatureFlagOn(FLAG_KEY, Collections.emptyMap())).isTrue();
    }

    @Test
    void isFeatureFlagOnIsFalseForAnOffVariant() {
        givenResponse(responseWith("off"));

        assertThat(flagrEvalService.isFeatureFlagOn(FLAG_KEY)).isFalse();
    }

    private void givenResponse(PostEvaluationResponse response) {
        when(flagrEvalClient.evaluate(any(EvaluationContext.class))).thenReturn(response);
    }

    private static PostEvaluationResponse responseWith(String variantKey) {
        final PostEvaluationResponse response = new PostEvaluationResponse();
        response.setFlagID(1L);
        response.setFlagKey(FLAG_KEY);
        response.setVariantKey(variantKey);
        return response;
    }
}
