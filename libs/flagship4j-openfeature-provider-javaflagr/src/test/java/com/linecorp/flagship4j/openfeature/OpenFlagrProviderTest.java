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
package com.linecorp.flagship4j.openfeature;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.linecorp.flagship4j.javaflagr.OpenFlagr;
import com.linecorp.flagship4j.javaflagr.models.EvaluationResult;

import dev.openfeature.sdk.ErrorCode;
import dev.openfeature.sdk.ImmutableContext;
import dev.openfeature.sdk.MutableContext;
import dev.openfeature.sdk.MutableStructure;
import dev.openfeature.sdk.ProviderEvaluation;
import dev.openfeature.sdk.Reason;
import dev.openfeature.sdk.Value;

@ExtendWith(MockitoExtension.class)
class OpenFlagrProviderTest {

    private static final String FLAG_KEY = "hello-world-enabled";

    @Mock
    private OpenFlagr openFlagr;

    @Captor
    private ArgumentCaptor<com.linecorp.flagship4j.javaflagr.models.EvaluationContext> contextCaptor;

    private OpenFlagrProvider provider;

    @BeforeEach
    void setUp() {
        provider = new OpenFlagrProvider(openFlagr);
    }

    @Test
    void exposesItsProviderName() {
        assertEquals("OpenFlagr Provider", provider.getMetadata().getName());
    }

    // ------------------------------------------------------------------ boolean

    @ParameterizedTest
    @CsvSource({ "on,true", "true,true", "1,true", "off,false", "false,false", "0,false" })
    void booleanEvaluationMapsEveryEffectiveVariantAlias(String variantKey, boolean expected) {
        givenEvaluation(variantKey);

        final ProviderEvaluation<Boolean> evaluation =
                provider.getBooleanEvaluation(FLAG_KEY, !expected, new ImmutableContext());

        assertEquals(expected, evaluation.getValue());
        assertEquals(Reason.DEFAULT.name(), evaluation.getReason());
        assertEquals(variantKey, evaluation.getVariant());
        assertNull(evaluation.getErrorCode());
    }

    @Test
    void booleanEvaluationFallsBackToTheDefaultValueOnANonBooleanVariant() {
        givenEvaluation("control");

        final ProviderEvaluation<Boolean> evaluation =
                provider.getBooleanEvaluation(FLAG_KEY, true, new ImmutableContext());

        assertTrue(evaluation.getValue());
        assertEquals(ErrorCode.PARSE_ERROR, evaluation.getErrorCode());
        assertEquals(Reason.ERROR.name(), evaluation.getReason());
        assertEquals("control", evaluation.getVariant());
    }

    @Test
    void booleanEvaluationReportsAMissingFlag() {
        givenNoEvaluation();

        final ProviderEvaluation<Boolean> evaluation =
                provider.getBooleanEvaluation(FLAG_KEY, true, new ImmutableContext());

        assertTrue(evaluation.getValue());
        assertEquals(ErrorCode.FLAG_NOT_FOUND, evaluation.getErrorCode());
        assertEquals(Reason.ERROR.name(), evaluation.getReason());
        assertNull(evaluation.getVariant());
    }

    // ------------------------------------------------------------------ string

    @Test
    void stringEvaluationReturnsTheVariantKey() {
        givenEvaluation("treatment");

        final ProviderEvaluation<String> evaluation =
                provider.getStringEvaluation(FLAG_KEY, "fallback", new ImmutableContext());

        assertEquals("treatment", evaluation.getValue());
        assertEquals(Reason.DEFAULT.name(), evaluation.getReason());
        assertEquals("treatment", evaluation.getVariant());
        assertNull(evaluation.getErrorCode());
    }

    @Test
    void stringEvaluationFallsBackToTheDefaultValueOnAnEmptyVariant() {
        givenEvaluation("");

        final ProviderEvaluation<String> evaluation =
                provider.getStringEvaluation(FLAG_KEY, "fallback", new ImmutableContext());

        assertEquals("fallback", evaluation.getValue());
        assertEquals(ErrorCode.GENERAL, evaluation.getErrorCode());
        assertEquals(Reason.ERROR.name(), evaluation.getReason());
        assertNull(evaluation.getVariant());
    }

    @Test
    void stringEvaluationReportsAMissingFlag() {
        givenNoEvaluation();

        final ProviderEvaluation<String> evaluation =
                provider.getStringEvaluation(FLAG_KEY, "fallback", new ImmutableContext());

        assertEquals("fallback", evaluation.getValue());
        assertEquals(ErrorCode.FLAG_NOT_FOUND, evaluation.getErrorCode());
    }

    // ------------------------------------------------------------------ integer

    @Test
    void integerEvaluationParsesTheVariantKey() {
        givenEvaluation("42");

        final ProviderEvaluation<Integer> evaluation =
                provider.getIntegerEvaluation(FLAG_KEY, 0, new ImmutableContext());

        assertEquals(42, evaluation.getValue());
        assertEquals(Reason.DEFAULT.name(), evaluation.getReason());
        assertEquals("42", evaluation.getVariant());
        assertNull(evaluation.getErrorCode());
    }

    @Test
    void integerEvaluationFallsBackToTheDefaultValueOnANonNumericVariant() {
        givenEvaluation("on");

        final ProviderEvaluation<Integer> evaluation =
                provider.getIntegerEvaluation(FLAG_KEY, 7, new ImmutableContext());

        assertEquals(7, evaluation.getValue());
        assertEquals(ErrorCode.PARSE_ERROR, evaluation.getErrorCode());
        assertEquals(Reason.ERROR.name(), evaluation.getReason());
        assertEquals("on", evaluation.getVariant());
    }

    @Test
    void integerEvaluationReportsAMissingFlag() {
        givenNoEvaluation();

        final ProviderEvaluation<Integer> evaluation =
                provider.getIntegerEvaluation(FLAG_KEY, 7, new ImmutableContext());

        assertEquals(7, evaluation.getValue());
        assertEquals(ErrorCode.FLAG_NOT_FOUND, evaluation.getErrorCode());
    }

    // ------------------------------------------------------------------ double

    @Test
    void doubleEvaluationParsesTheVariantKey() {
        givenEvaluation("1.5");

        final ProviderEvaluation<Double> evaluation =
                provider.getDoubleEvaluation(FLAG_KEY, 0.0, new ImmutableContext());

        assertEquals(1.5, evaluation.getValue());
        assertEquals(Reason.DEFAULT.name(), evaluation.getReason());
        assertEquals("1.5", evaluation.getVariant());
        assertNull(evaluation.getErrorCode());
    }

    @Test
    void doubleEvaluationFallsBackToTheDefaultValueOnANonNumericVariant() {
        givenEvaluation("on");

        final ProviderEvaluation<Double> evaluation =
                provider.getDoubleEvaluation(FLAG_KEY, 2.5, new ImmutableContext());

        assertEquals(2.5, evaluation.getValue());
        assertEquals(ErrorCode.PARSE_ERROR, evaluation.getErrorCode());
        assertEquals(Reason.ERROR.name(), evaluation.getReason());
        assertEquals("on", evaluation.getVariant());
    }

    @Test
    void doubleEvaluationReportsAMissingFlag() {
        givenNoEvaluation();

        final ProviderEvaluation<Double> evaluation =
                provider.getDoubleEvaluation(FLAG_KEY, 2.5, new ImmutableContext());

        assertEquals(2.5, evaluation.getValue());
        assertEquals(ErrorCode.FLAG_NOT_FOUND, evaluation.getErrorCode());
    }

    // ------------------------------------------------------------------ object

    @Test
    void objectEvaluationExposesTheVariantAttachmentAsAStructure() {
        final Map<String, Object> attachment = new HashMap<>();
        attachment.put("enabled", true);
        attachment.put("ratio", 0.5);
        attachment.put("label", "canary");
        givenEvaluation(EvaluationResult.builder()
                                        .flagId(42L)
                                        .flagKey(FLAG_KEY)
                                        .variantKey("on")
                                        .variantAttachment(attachment)
                                        .build());

        final ProviderEvaluation<Value> evaluation =
                provider.getObjectEvaluation(FLAG_KEY, new Value(), new ImmutableContext());

        assertEquals(Reason.DEFAULT.name(), evaluation.getReason());
        assertEquals("on", evaluation.getVariant());
        assertNull(evaluation.getErrorCode());
        assertEquals(Boolean.TRUE, evaluation.getValue().asStructure().getValue("enabled").asBoolean());
        assertEquals(0.5, evaluation.getValue().asStructure().getValue("ratio").asDouble());
        assertEquals("canary", evaluation.getValue().asStructure().getValue("label").asString());
    }

    @Test
    void objectEvaluationReportsAMissingFlag() {
        givenNoEvaluation();
        final Value defaultValue = new Value("fallback");

        final ProviderEvaluation<Value> evaluation =
                provider.getObjectEvaluation(FLAG_KEY, defaultValue, new ImmutableContext());

        assertEquals(defaultValue, evaluation.getValue());
        assertEquals(ErrorCode.FLAG_NOT_FOUND, evaluation.getErrorCode());
        assertEquals(Reason.ERROR.name(), evaluation.getReason());
        assertNull(evaluation.getVariant());
    }

    @Test
    void objectEvaluationSupportsAnEmptyAttachment() {
        givenEvaluation(EvaluationResult.builder()
                                        .flagId(42L)
                                        .variantKey("on")
                                        .variantAttachment(Collections.emptyMap())
                                        .build());

        final ProviderEvaluation<Value> evaluation =
                provider.getObjectEvaluation(FLAG_KEY, new Value(), new ImmutableContext());

        assertTrue(evaluation.getValue().asStructure().keySet().isEmpty());
    }

    // ------------------------------------------------------------------ context translation

    @Test
    void forwardsTheOpenFeatureContextToOpenFlagr() {
        givenEvaluation("on");
        final MutableContext context = new MutableContext("user-1")
                .add("entityType", "user")
                .add("entityContext", new MutableStructure().add("country", "JP"))
                .add("enableDebug", true)
                .add("flagId", 42);

        provider.getBooleanEvaluation(FLAG_KEY, false, context);

        verify(openFlagr).evaluate(contextCaptor.capture());
        assertEquals(FLAG_KEY, contextCaptor.getValue().getFlagKey());
        assertEquals("user-1", contextCaptor.getValue().getEntityId());
        assertEquals("user", contextCaptor.getValue().getEntityType());
        assertEquals("JP", contextCaptor.getValue().getEntityContext().get("country"));
        assertEquals(Boolean.TRUE, contextCaptor.getValue().getEnableDebug());
        assertEquals(42L, contextCaptor.getValue().getFlagId());
    }

    // ------------------------------------------------------------------ helpers

    private void givenEvaluation(String variantKey) {
        givenEvaluation(EvaluationResult.builder()
                                        .flagId(42L)
                                        .flagKey(FLAG_KEY)
                                        .variantKey(variantKey)
                                        .variantAttachment(Collections.emptyMap())
                                        .build());
    }

    private void givenEvaluation(EvaluationResult result) {
        when(openFlagr.evaluate(any(com.linecorp.flagship4j.javaflagr.models.EvaluationContext.class)))
                .thenReturn(Optional.of(result));
    }

    private void givenNoEvaluation() {
        when(openFlagr.evaluate(any(com.linecorp.flagship4j.javaflagr.models.EvaluationContext.class)))
                .thenReturn(Optional.empty());
    }
}
