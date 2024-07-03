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
package com.linecorp.flagship4j.javaflagr;

import java.util.Map;
import java.util.Optional;

import com.linecorp.flagship4j.javaflagr.clients.DefaultOpenFlagrApiClient;
import com.linecorp.flagship4j.javaflagr.clients.OpenFlagrApiClient;
import com.linecorp.flagship4j.javaflagr.configs.OpenFlagrConfig;
import com.linecorp.flagship4j.javaflagr.enums.EffectiveVariant;
import com.linecorp.flagship4j.javaflagr.models.EvaluationContext;
import com.linecorp.flagship4j.javaflagr.models.EvaluationResult;
import com.linecorp.flagship4j.javaflagr.exceptions.OpenFlagrException;
import com.linecorp.flagship4j.javaflagr.exceptions.OpenFlagrNoFlagKeyException;
import com.linecorp.flagship4j.javaflagr.exceptions.OpenFlagrNoVariantException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DefaultOpenFlagr implements OpenFlagr {

    private OpenFlagrApiClient openFlagrApiClient;

    public DefaultOpenFlagr(OpenFlagrConfig openFlagrConfig) {
        this.openFlagrApiClient = new DefaultOpenFlagrApiClient(openFlagrConfig);
    }

    @Override
    public Boolean isFeatureFlagOn(String flagKey) {
        return isFeatureFlagOn(flagKey, null, null);
    }

    @Override
    public Boolean isFeatureFlagOn(String flagKey, String entityId) {
        return isFeatureFlagOn(flagKey, entityId, null);
    }

    @Override
    public Boolean isFeatureFlagOn(String flagKey, Map<String, Object> entityContext) {
        return isFeatureFlagOn(flagKey, null, entityContext);
    }

    @Override
    public Boolean isFeatureFlagOn(String flagKey, String entityId, Map<String, Object> entityContext) {
        return isFeatureFlagOn(EvaluationContext.builder()
                                                .entityId(entityId)
                                                .entityContext(entityContext)
                                                .flagKey(flagKey)
                                                .enableDebug(true)
                                                .build());
    }

    @Override
    public Boolean isFeatureFlagOn(EvaluationContext evaluationContext) {
        return evaluateByDefaultVariantKey(evaluationContext);
    }

    @Override
    public Optional<String> getVariantKey(String flagKey) {
        return this.getVariantKey(flagKey, null, null);
    }

    @Override
    public Optional<String> getVariantKey(String flagKey, Map<String, Object> entityContext) {
        return this.getVariantKey(flagKey, null, entityContext);
    }

    @Override
    public Optional<String> getVariantKey(String flagKey, String entityId) {
        return this.getVariantKey(flagKey, entityId, null);
    }

    @Override
    public Optional<String> getVariantKey(String flagKey, String entityId, Map<String, Object> entityContext) {
        return getVariantKey(EvaluationContext.builder()
                .entityId(entityId)
                .entityContext(entityContext)
                .flagKey(flagKey)
                .enableDebug(true)
                .build());
    }

    @Override
    public Optional<String> getVariantKey(EvaluationContext evaluationContext) {
        return getFlagrVariantKey(evaluationContext);
    }

    @Override
    public Optional<Map<String, Object>> getVariantAttachment(String flagKey) {
        return getVariantAttachment(flagKey, null, null);
    }

    @Override
    public Optional<Map<String, Object>> getVariantAttachment(String flagKey, String entityId) {
        return getVariantAttachment(flagKey, entityId, null);
    }

    @Override
    public Optional<Map<String, Object>> getVariantAttachment(String flagKey, Map<String, Object> entityContext) {
        return getVariantAttachment(flagKey, null, entityContext);
    }

    @Override
    public Optional<Map<String, Object>> getVariantAttachment(
            String flagKey,
            String entityId,
            Map<String, Object> entityContext) {
        return getVariantAttachment(EvaluationContext.builder()
                .entityId(entityId)
                .entityContext(entityContext)
                .flagKey(flagKey)
                .enableDebug(true)
                .build());
    }

    @Override
    public Optional<Map<String, Object>> getVariantAttachment(EvaluationContext evaluationContext) {
        return getFlagrVariantAttachment(evaluationContext);
    }

    @Override
    public Optional<EvaluationResult> evaluate(EvaluationContext evaluationContext) {
        return getEvaluationResult(evaluationContext);
    }

    private Boolean evaluateByDefaultVariantKey(EvaluationContext evaluationContext) {
        try {
            final EffectiveVariant effectiveVariant = getFlagrVariantKey(evaluationContext)
                    .filter(EffectiveVariant::isValid)
                    .map(EffectiveVariant::fromValue)
                    .orElseThrow(() -> new OpenFlagrException(String.format(
                            "Received variant value is wrong, which should be 'on' or 'off'."
                                    + " Check distribution of flag key [%s] on UI panel.",
                            evaluationContext.getFlagKey())));

            return EffectiveVariant.ON.equals(effectiveVariant);
        } catch (OpenFlagrException e) {
            log.error(e.getMessage());
            return false;
        } catch (OpenFlagrNoFlagKeyException e) {
            log.warn(e.getMessage());
            return false;
        } catch (OpenFlagrNoVariantException e) {
            log.info(e.getMessage());
            return false;
        } catch (Exception e) {
            log.error("Unexpected error. " + e.getMessage());
            return false;
        }
    }

    private Optional<String> getFlagrVariantKey(EvaluationContext evaluationContext) {
        return getEvaluationResult(evaluationContext)
                .map(EvaluationResult::getVariantKey);
    }

    public Optional<Map<String, Object>> getFlagrVariantAttachment(EvaluationContext evaluationContext) {
        return getEvaluationResult(evaluationContext)
                .map(EvaluationResult::getVariantAttachment);
    }

    private Optional<EvaluationResult> getEvaluationResult(EvaluationContext evaluationContext) {
        try {
            EvaluationResult evaluationResult = openFlagrApiClient.postEvaluation(evaluationContext);
            validateReturnData(evaluationResult);
            return Optional.ofNullable(evaluationResult);
        } catch (OpenFlagrException e) {
            log.error(e.getMessage());
            return Optional.empty();
        } catch (OpenFlagrNoFlagKeyException e) {
            log.warn(e.getMessage());
            return Optional.empty();
        } catch (OpenFlagrNoVariantException e) {
            log.info(e.getMessage());
            return Optional.empty();
        } catch (Exception e) {
            log.error("Unexpected error. " + e.getMessage());
            return Optional.empty();
        }
    }

    private void validateReturnData(EvaluationResult evaluationResult) {
        if (evaluationResult.getFlagId() == null) {
            throw new OpenFlagrNoFlagKeyException("Flag key not found. Please check UI panel.");
        }
        if (evaluationResult.getVariantKey() == null) {
            throw new OpenFlagrNoVariantException("No variant key found. Check rollout and distribution on UI panel.");
        }
    }

}
