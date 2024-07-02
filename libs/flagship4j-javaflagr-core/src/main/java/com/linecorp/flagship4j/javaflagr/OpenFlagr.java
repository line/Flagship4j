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
package com.linecorp.flagship4j.javaflagr;

import com.linecorp.flagship4j.javaflagr.models.EvaluationContext;
import com.linecorp.flagship4j.javaflagr.models.EvaluationResult;

import java.util.Map;
import java.util.Optional;

public interface OpenFlagr {

    Boolean isFeatureFlagOn(String flagKey);

    Boolean isFeatureFlagOn(String flagKey, String entityId);

    Boolean isFeatureFlagOn(String flagKey, Map<String, Object> entityContext);

    Boolean isFeatureFlagOn(String flagKey, String entityId, Map<String, Object> entityContext);

    Boolean isFeatureFlagOn(EvaluationContext evaluationContext);

    Optional<String> getVariantKey(String flagKey);

    Optional<String> getVariantKey(String flagKey, String entityId);

    Optional<String> getVariantKey(String flagKey, Map<String, Object> entityContext);

    Optional<String> getVariantKey(String flagKey, String entityId, Map<String, Object> entityContext);

    Optional<String> getVariantKey(EvaluationContext evaluationContext);

    Optional<Map<String, Object>> getVariantAttachment(String flagKey);

    Optional<Map<String, Object>> getVariantAttachment(String flagKey, String entityId);

    Optional<Map<String, Object>> getVariantAttachment(String flagKey, Map<String, Object> entityContext);

    Optional<Map<String, Object>> getVariantAttachment(String flagKey, String entityId,
            Map<String, Object> entityContext);

    Optional<Map<String, Object>> getVariantAttachment(EvaluationContext evaluationContext);

    Optional<EvaluationResult> evaluate(EvaluationContext evaluationContext);

}
