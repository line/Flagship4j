/*
 * Copyright 2024 LY Corporation
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
package com.linecorp.flagship4j.javaflagr.serivce;

import com.linecorp.flagship4j.javaflagr.annotation.TestService;
import com.linecorp.flagship4j.javaflagr.client.FlagrEvalClient;
import com.linecorp.flagship4j.javaflagr.model.EvaluationContext;
import com.linecorp.flagship4j.javaflagr.model.PostEvaluationResponse;
import com.linecorp.flagship4j.javaflagr.service.FlagrEvalService;
import com.linecorp.flagship4j.javaflagr.service.impl.FlagrEvalServiceImpl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import static com.linecorp.flagship4j.javaflagr.fixture.PostEvaluationResponseFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@TestService(components = FlagrEvalServiceImpl.class)
public class FlagrEvalServiceTest {

    @Autowired
    private FlagrEvalService flagrEvalService;

    @MockBean
    private FlagrEvalClient flagrEvalClient;

    @Test
    public void isFeatureFlagOnSuccess() throws Exception {
        PostEvaluationResponse givenResponse = mockSuccessPostEvaluationResponse();
        String givenFlagKey = "exist_feature_flag";

        when(flagrEvalClient.evaluate(any(EvaluationContext.class))).thenReturn(givenResponse);

        Boolean result = flagrEvalService.isFeatureFlagOn(givenFlagKey);

        assertThat(result)
                .as("Check returned variant is on.")
                .isTrue();
    }

    @Test
    public void isFeatureFlagOnSuccessWithEntityContext() throws Exception {
        PostEvaluationResponse givenResponse = mockSuccessPostEvaluationResponse();
        String givenFlagKey = "exist_feature_flag";
        String givenFieldName = "fieldName";
        String givenValue = "value";
        Map<Object, Object> givenEntityContext = Collections.singletonMap(givenFieldName, givenValue);

        when(flagrEvalClient.evaluate(any(EvaluationContext.class))).thenReturn(givenResponse);

        Boolean result = flagrEvalService.isFeatureFlagOn(givenFlagKey, givenEntityContext);

        assertThat(result)
                .as("Check returned variant is on.")
                .isTrue();

        verify(flagrEvalClient, times(1)).evaluate(
                argThat(ec -> ec.getEntityContext().get(givenFieldName).equals(givenValue)));
    }

    @Test
    public void isFeatureFlagOnFailFlagKeyNotFound() throws Exception {
        PostEvaluationResponse givenResponse = mockFlagKeyNotFoundPostEvaluationResponse();
        String givenFlagKey = "not_exist_flag";

        when(flagrEvalClient.evaluate(any(EvaluationContext.class))).thenReturn(givenResponse);

        Boolean result = flagrEvalService.isFeatureFlagOn(givenFlagKey);

        assertThat(result)
                .as("Check result is false when flag key not found.")
                .isFalse();
    }

    @Test
    public void isFeatureFlagOnFailVariantKeyNotFound() throws Exception {
        PostEvaluationResponse givenResponse = mockVariantKeyNotFoundPostEvaluationResponse();
        String givenFlagKey = "exist_feature_flag";

        when(flagrEvalClient.evaluate(any(EvaluationContext.class))).thenReturn(givenResponse);

        Boolean result = flagrEvalService.isFeatureFlagOn(givenFlagKey);

        assertThat(result)
                .as("Check result is false when variant key not found.")
                .isFalse();
    }

    @Test
    public void isFeatureFlagOnFailVariantKeyNotMatch() throws Exception {
        PostEvaluationResponse givenResponse = mockVariantKeyNotMatchPostEvaluationResponse();
        String givenFlagKey = "exist_feature_flag";

        when(flagrEvalClient.evaluate(any(EvaluationContext.class))).thenReturn(givenResponse);

        Boolean result = flagrEvalService.isFeatureFlagOn(givenFlagKey);

        assertThat(result)
                .as("Check result is false when variant key not match.")
                .isFalse();
    }

    @Test
    public void isFeatureFlagOnSuccessByVariantKey() throws Exception {
        PostEvaluationResponse givenResponse = mockSuccessPostEvaluationResponse();
        String givenFlagKey = "exist_feature_flag";
        String givenVariantKey = "on";

        when(flagrEvalClient.evaluate(any(EvaluationContext.class))).thenReturn(givenResponse);

        Boolean result = flagrEvalService.isFeatureFlagOn(givenFlagKey, givenVariantKey);

        assertThat(result)
                .as("Check returned variant is on.")
                .isTrue();
    }

    @Test
    public void isFeatureFlagOnSuccessByVariantKeyWithEntityContext() throws Exception {
        PostEvaluationResponse givenResponse = mockSuccessPostEvaluationResponse();
        String givenFlagKey = "exist_feature_flag";
        String givenVariantKey = "on";
        String givenFieldName = "fieldName";
        String givenValue = "value";
        Map<Object, Object> givenEntityContext = Collections.singletonMap(givenFieldName, givenValue);

        when(flagrEvalClient.evaluate(any(EvaluationContext.class))).thenReturn(givenResponse);

        Boolean result = flagrEvalService.isFeatureFlagOn(givenFlagKey, givenVariantKey, givenEntityContext);

        assertThat(result)
                .as("Check returned variant is on.")
                .isTrue();

        verify(flagrEvalClient, times(1)).evaluate(
                argThat(ec -> ec.getEntityContext().get(givenFieldName).equals(givenValue)));
    }

    @Test
    public void isFeatureFlagOnFailFlagKeyNotFoundByVariantKey() throws Exception {
        PostEvaluationResponse givenResponse = mockFlagKeyNotFoundPostEvaluationResponse();
        String givenFlagKey = "not_exist_flag";
        String givenVariantKey = "on";

        when(flagrEvalClient.evaluate(any(EvaluationContext.class))).thenReturn(givenResponse);

        Boolean result = flagrEvalService.isFeatureFlagOn(givenFlagKey, givenVariantKey);

        assertThat(result)
                .as("Check result is false when flag key not found.")
                .isFalse();
    }

    @Test
    public void isFeatureFlagOnFailVariantKeyNotFoundByVariantKey() throws Exception {
        PostEvaluationResponse givenResponse = mockVariantKeyNotFoundPostEvaluationResponse();
        String givenFlagKey = "exist_feature_flag";
        String givenVariantKey = "on";

        when(flagrEvalClient.evaluate(any(EvaluationContext.class))).thenReturn(givenResponse);

        Boolean result = flagrEvalService.isFeatureFlagOn(givenFlagKey, givenVariantKey);

        assertThat(result)
                .as("Check result is false when variant key not found.")
                .isFalse();
    }

    @Test
    public void isFeatureFlagOnFailVariantKeyNotMatchByVariantKey() throws Exception {
        PostEvaluationResponse givenResponse = mockVariantKeyNotMatchPostEvaluationResponse();
        String givenFlagKey = "exist_feature_flag";
        String givenVariantKey = "on";

        when(flagrEvalClient.evaluate(any(EvaluationContext.class))).thenReturn(givenResponse);

        Boolean result = flagrEvalService.isFeatureFlagOn(givenFlagKey, givenVariantKey);

        assertThat(result)
                .as("Check result is false when variant key not match.")
                .isFalse();
    }

    @Test
    public void getVariantKey() throws Exception {
        String givenFlagKey = "exist_feature_flag";
        String givenVariantKey = "custom_variant_key";
        PostEvaluationResponse givenResponse = mockSuccessPostEvaluationResponse(givenFlagKey, givenVariantKey);

        when(flagrEvalClient.evaluate(any(EvaluationContext.class))).thenReturn(givenResponse);

        Optional<String> result = flagrEvalService.getVariantKey(givenFlagKey);

        assertThat(result)
                .hasValue("custom_variant_key");
    }

    @Test
    public void getVariantKeyWithEntityContext() throws Exception {
        String givenFlagKey = "exist_feature_flag";
        String givenVariantKey = "custom_variant_key";
        String givenFieldName = "fieldName";
        String givenValue = "value";
        PostEvaluationResponse givenResponse = mockSuccessPostEvaluationResponse(givenFlagKey, givenVariantKey);
        Map<Object, Object> givenEntityContext = Collections.singletonMap(givenFieldName, givenValue);

        when(flagrEvalClient.evaluate(any(EvaluationContext.class))).thenReturn(givenResponse);

        Optional<String> result = flagrEvalService.getVariantKey(givenFlagKey, givenEntityContext);

        assertThat(result)
                .hasValue("custom_variant_key");

        verify(flagrEvalClient, times(1)).evaluate(
                argThat(ec -> ec.getEntityContext().get(givenFieldName).equals(givenValue)));
    }

    @Test
    public void getVariantKeyWhenVariantKeyIsNull() throws Exception {
        String givenFlagKey = "exist_feature_flag";
        PostEvaluationResponse givenResponse = mockSuccessPostEvaluationResponse(givenFlagKey, null);

        when(flagrEvalClient.evaluate(any(EvaluationContext.class))).thenReturn(givenResponse);

        Optional<String> result = flagrEvalService.getVariantKey(givenFlagKey);

        assertThat(result).isEmpty();
    }
}
