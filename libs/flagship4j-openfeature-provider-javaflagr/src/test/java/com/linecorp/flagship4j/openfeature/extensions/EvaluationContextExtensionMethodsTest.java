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
package com.linecorp.flagship4j.openfeature.extensions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import com.linecorp.flagship4j.javaflagr.models.EvaluationContext;

import dev.openfeature.sdk.ImmutableContext;
import dev.openfeature.sdk.MutableContext;
import dev.openfeature.sdk.MutableStructure;

class EvaluationContextExtensionMethodsTest {

    private static final String FLAG_KEY = "hello-world-enabled";

    @Test
    void translatesEverySupportedAttribute() {
        final MutableContext source = new MutableContext("user-1")
                .add("entityType", "user")
                .add("entityContext", new MutableStructure().add("country", "JP").add("age", 20))
                .add("enableDebug", true)
                .add("flagId", 42);

        final EvaluationContext result =
                EvaluationContextExtensionMethods.toOpenFlagrEvaluationContext(source, FLAG_KEY);

        assertEquals(FLAG_KEY, result.getFlagKey());
        assertEquals("user-1", result.getEntityId());
        assertEquals("user", result.getEntityType());
        assertEquals("JP", result.getEntityContext().get("country"));
        assertEquals(Integer.valueOf(20), result.getEntityContext().get("age"));
        assertEquals(Boolean.TRUE, result.getEnableDebug());
        assertEquals(42L, result.getFlagId());
    }

    /**
     * An OpenFeature context without a targeting key reports it as an empty string rather than
     * {@code null}, so the entity id is forwarded as an empty string instead of being left unset.
     */
    @Test
    void keepsOnlyTheFlagKeyAndAnEmptyEntityIdWhenTheSourceContextIsEmpty() {
        final EvaluationContext result =
                EvaluationContextExtensionMethods.toOpenFlagrEvaluationContext(new ImmutableContext(), FLAG_KEY);

        assertEquals(FLAG_KEY, result.getFlagKey());
        assertEquals("", result.getEntityId());
        assertNull(result.getEntityType());
        assertNull(result.getEntityContext());
        assertNull(result.getEnableDebug());
        assertNull(result.getFlagId());
    }

    @Test
    void translatesTheTargetingKeyOnItsOwn() {
        final EvaluationContext result = EvaluationContextExtensionMethods
                .toOpenFlagrEvaluationContext(new ImmutableContext("user-1"), FLAG_KEY);

        assertEquals("user-1", result.getEntityId());
        assertNull(result.getEntityType());
    }

    @Test
    void leavesTheEntityIdUnsetWhenThereIsNoTargetingKeyAtAll() {
        final MutableContext source = new MutableContext();
        source.setTargetingKey(null);

        final EvaluationContext result =
                EvaluationContextExtensionMethods.toOpenFlagrEvaluationContext(source, FLAG_KEY);

        assertNull(result.getEntityId());
        assertEquals(FLAG_KEY, result.getFlagKey());
    }

    @Test
    void translatesAPartiallyPopulatedContext() {
        final MutableContext source = new MutableContext().add("enableDebug", false);

        final EvaluationContext result =
                EvaluationContextExtensionMethods.toOpenFlagrEvaluationContext(source, FLAG_KEY);

        assertEquals("", result.getEntityId());
        assertEquals(Boolean.FALSE, result.getEnableDebug());
        assertNull(result.getFlagId());
    }
}
