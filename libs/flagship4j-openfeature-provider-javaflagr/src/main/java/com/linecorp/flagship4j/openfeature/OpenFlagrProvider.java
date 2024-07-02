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
package com.linecorp.flagship4j.openfeature;

import com.linecorp.flagship4j.javaflagr.OpenFlagr;
import com.linecorp.flagship4j.javaflagr.enums.EffectiveVariant;
import com.linecorp.flagship4j.javaflagr.models.EvaluationResult;
import com.linecorp.flagship4j.openfeature.extensions.EvaluationContextExtensionMethods;

import dev.openfeature.sdk.ErrorCode;
import dev.openfeature.sdk.EvaluationContext;
import dev.openfeature.sdk.FeatureProvider;
import dev.openfeature.sdk.Metadata;
import dev.openfeature.sdk.ProviderEvaluation;
import dev.openfeature.sdk.Reason;
import dev.openfeature.sdk.Structure;
import dev.openfeature.sdk.Value;
import lombok.RequiredArgsConstructor;
import lombok.experimental.ExtensionMethod;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

@ExtensionMethod({ EvaluationContextExtensionMethods.class })
@RequiredArgsConstructor
@Slf4j
public class OpenFlagrProvider implements FeatureProvider {

    private static final String PROVIDER_NAME = "OpenFlagr Provider";

    private final OpenFlagr openFlagr;

    @Override
    public Metadata getMetadata() {
        return () -> PROVIDER_NAME;
    }

    @Override
    public ProviderEvaluation<Boolean> getBooleanEvaluation(String key, Boolean defaultValue, EvaluationContext ctx) {
        ProviderEvaluation<Boolean> evaluation = openFlagr.evaluate(ctx.toOpenFlagrEvaluationContext(key))
                 .map(result -> getBooleanEvaluation(result, defaultValue))
                 .orElseGet(() -> buildFlagNotFoundEvaluation(defaultValue));
        logEvaluationResult(evaluation);
        return evaluation; 
    }

    @Override
    public ProviderEvaluation<String> getStringEvaluation(String key, String defaultValue, EvaluationContext ctx) {
        ProviderEvaluation<String> evaluation =  openFlagr.evaluate(ctx.toOpenFlagrEvaluationContext(key))
                .map(result -> getStringEvaluation(result, defaultValue))
                .orElseGet(() -> buildFlagNotFoundEvaluation(defaultValue));
        logEvaluationResult(evaluation);
        return evaluation;
    }

    @Override
    public ProviderEvaluation<Integer> getIntegerEvaluation(String key, Integer defaultValue, EvaluationContext ctx) {
        ProviderEvaluation<Integer> evaluation =  openFlagr.evaluate(ctx.toOpenFlagrEvaluationContext(key))
                .map(result -> getIntegerEvaluation(result, defaultValue))
                .orElseGet(() -> buildFlagNotFoundEvaluation(defaultValue));
        logEvaluationResult(evaluation);
        return evaluation;
    }

    @Override
    public ProviderEvaluation<Double> getDoubleEvaluation(String key, Double defaultValue, EvaluationContext ctx) {
        ProviderEvaluation<Double> evaluation =  openFlagr.evaluate(ctx.toOpenFlagrEvaluationContext(key))
                .map(result -> getDoubleEvaluation(result, defaultValue))
                .orElseGet(() -> buildFlagNotFoundEvaluation(defaultValue));
        logEvaluationResult(evaluation);
        return evaluation;
    }

    private <T> void logEvaluationResult(ProviderEvaluation<T> evaluation) {
        log.debug("evaluation result value = {}", evaluation.getValue());
    }

    @Override
    public ProviderEvaluation<Value> getObjectEvaluation(String key, Value defaultValue, EvaluationContext ctx) {
        ProviderEvaluation<Value> evaluation =  openFlagr.evaluate(ctx.toOpenFlagrEvaluationContext(key))
                .map(result -> getObjectEvaluation(result, defaultValue))
                .orElseGet(() -> buildFlagNotFoundEvaluation(defaultValue));
        logEvaluationResult(evaluation);
        return evaluation;
    }

    private ProviderEvaluation<Boolean> getBooleanEvaluation(EvaluationResult evaluationResult, Boolean defaultValue) {
        String variantKey = evaluationResult.getVariantKey();

        if (!EffectiveVariant.isValid(variantKey)) {
            return buildEvaluation(
                    defaultValue,
                    ErrorCode.PARSE_ERROR,
                    Reason.ERROR,
                    variantKey);
        }

        return buildEvaluation(
                EffectiveVariant.ON.equals(EffectiveVariant.fromValue(variantKey)),
                null,
                Reason.DEFAULT,
                variantKey);
    }

    private ProviderEvaluation<String> getStringEvaluation(EvaluationResult evaluationResult, String defaultValue) {
        String variantKey = evaluationResult.getVariantKey();

        if (StringUtils.isEmpty(variantKey)) {
            return buildEvaluation(
                    defaultValue,
                    ErrorCode.GENERAL,
                    Reason.ERROR,
                    null);
        }

        return buildEvaluation(
                variantKey,
                null,
                Reason.DEFAULT,
                variantKey);
    }

    private ProviderEvaluation<Integer> getIntegerEvaluation(EvaluationResult evaluationResult, Integer defaultValue) {
        String variantKey = evaluationResult.getVariantKey();

        try {
            return buildEvaluation(
                    Integer.valueOf(variantKey),
                    null,
                    Reason.DEFAULT,
                    variantKey);
        } catch (NumberFormatException ex) {
            return buildEvaluation(
                    defaultValue,
                    ErrorCode.PARSE_ERROR,
                    Reason.ERROR,
                    variantKey);
        }
    }

    private ProviderEvaluation<Double> getDoubleEvaluation(EvaluationResult evaluationResult, Double defaultValue) {
        String variantKey = evaluationResult.getVariantKey();

        try {
            return buildEvaluation(
                    Double.valueOf(variantKey),
                    null,
                    Reason.DEFAULT,
                    variantKey);
        } catch (NumberFormatException ex) {
            return buildEvaluation(
                    defaultValue,
                    ErrorCode.PARSE_ERROR,
                    Reason.ERROR,
                    variantKey);
        }
    }

    private ProviderEvaluation<Value> getObjectEvaluation(EvaluationResult evaluationResult, Value defaultValue) {
        String variantKey = evaluationResult.getVariantKey();
        Map<String, Object> variantAttachment = evaluationResult.getVariantAttachment();

        return buildEvaluation(
                new Value(Structure.mapToStructure(variantAttachment)),
                null,
                Reason.DEFAULT,
                variantKey);
    }

    private <T> ProviderEvaluation<T> buildFlagNotFoundEvaluation(T defaultValue) {
        return buildEvaluation(
                defaultValue,
                ErrorCode.FLAG_NOT_FOUND,
                Reason.ERROR,
                null);
    }

    private <T> ProviderEvaluation<T> buildEvaluation(
            T value, ErrorCode errorCode, Reason reason, String variant) {
        ProviderEvaluation.ProviderEvaluationBuilder<T> evaluationBuilder = ProviderEvaluation.<T>builder()
                .value(value);

        if (errorCode != null) {
            evaluationBuilder.errorCode(errorCode);
        }
        if (reason != null) {
            evaluationBuilder.reason(reason.name());
        }
        if (variant != null) {
            evaluationBuilder.variant(variant);
        }

        return evaluationBuilder.build();
    }

}
