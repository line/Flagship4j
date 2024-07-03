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
package com.linecorp.flagship4j.javaflagr.service.impl;

import java.util.Map;
import java.util.Optional;

import com.linecorp.flagship4j.javaflagr.client.FlagrEvalClient;
import com.linecorp.flagship4j.javaflagr.exception.FlagrException;
import com.linecorp.flagship4j.javaflagr.model.EvaluationContext;
import com.linecorp.flagship4j.javaflagr.model.PostEvaluationResponse;
import com.linecorp.flagship4j.javaflagr.model.enums.EffectiveVariant;
import com.linecorp.flagship4j.javaflagr.service.FlagrEvalService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class FlagrEvalServiceImpl implements FlagrEvalService {

    private final FlagrEvalClient flagrEvalClient;

    @Override
    public Boolean isFeatureFlagOn(String flagKey) {
        EvaluationContext evaluationContext = new EvaluationContext();
        evaluationContext.setFlagKey(flagKey);
        evaluationContext.setEnableDebug(true);

        return evaluateByDefaultVariantKey(evaluationContext);
    }

    @Override
    public Boolean isFeatureFlagOn(String flagKey, Map<Object, Object> entityContext) {
        EvaluationContext evaluationContext = new EvaluationContext();
        evaluationContext.setFlagKey(flagKey);
        evaluationContext.setEnableDebug(true);
        evaluationContext.setEntityContext(entityContext);

        return evaluateByDefaultVariantKey(evaluationContext);
    }

    private Boolean evaluateByDefaultVariantKey(EvaluationContext evaluationContext) {
        try {
            final String variantKey = getFlagrVariantKey(evaluationContext)
                    .filter(EffectiveVariant::isValid)
                    .orElseThrow(() -> new FlagrException(String.format(
                            "Received variant value is wrong, which should be 'on' or 'off'."
                            + " Check distribution of flag key [%s] on UI panel.",
                            evaluationContext.getFlagKey())));

            return variantKey.equals(EffectiveVariant.ON.toValue());
        } catch (FlagrException e) {
            log.error(e.getMessage());
            return false;
        } catch (Exception e) {
            log.error("Unexpected error. " + e.getMessage());
            return false;
        }
    }

    @Override
    public Boolean isFeatureFlagOn(String flagKey, String variantKey) {
        EvaluationContext evaluationContext = new EvaluationContext();
        evaluationContext.setFlagKey(flagKey);
        evaluationContext.setEnableDebug(true);

        return evaluateByVariantKey(evaluationContext, variantKey);
    }

    @Override
    public Boolean isFeatureFlagOn(String flagKey, String variantKey, Map<Object, Object> entityContext) {
        EvaluationContext evaluationContext = new EvaluationContext();
        evaluationContext.setFlagKey(flagKey);
        evaluationContext.setEnableDebug(true);
        evaluationContext.setEntityContext(entityContext);

        return evaluateByVariantKey(evaluationContext, variantKey);
    }

    @Override
    public Optional<String> getVariantKey(String flagKey) {
        return this.getVariantKey(flagKey, null);
    }

    @Override
    public Optional<String> getVariantKey(String flagKey, Map<Object, Object> entityContext) {
        EvaluationContext evaluationContext = new EvaluationContext();
        evaluationContext.setFlagKey(flagKey);
        evaluationContext.setEnableDebug(true);
        if (entityContext != null) {
            evaluationContext.setEntityContext(entityContext);
        }

        return getFlagrVariantKey(evaluationContext);
    }
    
    private Boolean evaluateByVariantKey(EvaluationContext evaluationContext, String variantKey) {
        return getFlagrVariantKey(evaluationContext)
                .map(responseVariantKey -> responseVariantKey.equals(variantKey))
                .orElse(false);
    }

    private Optional<String> getFlagrVariantKey(EvaluationContext evaluationContext) {
        try {
            PostEvaluationResponse postEvaluationResponse = flagrEvalClient.evaluate(evaluationContext);
            validateReturnData(postEvaluationResponse);
            return Optional.ofNullable(postEvaluationResponse.getVariantKey());
        } catch (FlagrException e) {
            log.error(e.getMessage());
            return Optional.empty();
        } catch (Exception e) {
            log.error("Unexpected error. " + e.getMessage());
            return Optional.empty();
        }
    }

    private void validateReturnData(PostEvaluationResponse postEvaluationResponse) {
        if (postEvaluationResponse.getFlagID() == null) {
            throw new FlagrException("Flag key not found. Please check UI panel.");
        }
        if (postEvaluationResponse.getVariantKey() == null) {
            throw new FlagrException("No variant key found. Check rollout and distribution on UI panel.");
        }
    }
}
