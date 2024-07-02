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
package com.linecorp.flagship4j.openfeature.extensions;

import com.linecorp.flagship4j.javaflagr.models.EvaluationContext;

public class EvaluationContextExtensionMethods {

    public static EvaluationContext toOpenFlagrEvaluationContext(
            dev.openfeature.sdk.EvaluationContext source, String key) {
        EvaluationContext.EvaluationContextBuilder evaluationContextBuilder = EvaluationContext.builder().flagKey(key);

        if (source.getTargetingKey() != null) {
            evaluationContextBuilder.entityId(source.getTargetingKey());
        }

        if (source.getValue("entityType") != null) {
            evaluationContextBuilder.entityType(source.getValue("entityType").asString());
        }

        if (source.getValue("entityContext") != null) {
            evaluationContextBuilder.entityContext(source.getValue("entityContext").asStructure().asObjectMap());
        }

        if (source.getValue("enableDebug") != null) {
            evaluationContextBuilder.enableDebug(source.getValue("enableDebug").asBoolean());
        }

        if (source.getValue("flagId") != null) {
            evaluationContextBuilder.flagId(source.getValue("flagId").asInteger().longValue());
        }

        return evaluationContextBuilder.build();
    }

}
