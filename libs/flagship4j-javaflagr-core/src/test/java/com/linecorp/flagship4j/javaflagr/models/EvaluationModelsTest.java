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
package com.linecorp.flagship4j.javaflagr.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.Collections;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * Covers the Lombok generated accessors of the model classes and, more importantly, the Jackson
 * mapping between the OpenFlagr wire format (with its {@code *ID} / {@code msg} field names) and
 * the SDK model field names.
 */
class EvaluationModelsTest {

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

    @Test
    void evaluationResultIsDeserializedFromTheWireFieldNames() throws Exception {
        final String json = "{"
                + "\"flagID\":42,"
                + "\"flagKey\":\"hello-world-enabled\","
                + "\"flagSnapshotID\":596,"
                + "\"segmentID\":1,"
                + "\"variantID\":7,"
                + "\"variantKey\":\"on\","
                + "\"variantAttachment\":{\"enabled\":true},"
                + "\"evalContext\":{\"entityID\":\"debug\",\"flagID\":42,\"enableDebug\":true},"
                + "\"timestamp\":\"2026-07-30T05:00:00Z\","
                + "\"evalDebugLog\":{"
                + "\"msg\":\"matched all constraints\","
                + "\"segmentDebugLogs\":[{\"segmentID\":1,\"msg\":\"matched\"}]"
                + "}"
                + "}";

        final EvaluationResult result = objectMapper.readValue(json, EvaluationResult.class);

        assertEquals(42L, result.getFlagId());
        assertEquals("hello-world-enabled", result.getFlagKey());
        assertEquals(596L, result.getFlagSnapshotId());
        assertEquals(1L, result.getSegmentId());
        assertEquals(7L, result.getVariantId());
        assertEquals("on", result.getVariantKey());
        assertEquals(Boolean.TRUE, result.getVariantAttachment().get("enabled"));
        assertEquals(Instant.parse("2026-07-30T05:00:00Z"), result.getTimestamp());
        assertEquals("debug", result.getEvaluationContext().getEntityId());
        assertEquals(42L, result.getEvaluationContext().getFlagId());
        assertEquals(Boolean.TRUE, result.getEvaluationContext().getEnableDebug());
        assertEquals("matched all constraints", result.getEvaluationDebugLog().getMessage());
        assertEquals(1, result.getEvaluationDebugLog().getSegmentDebugLogs().size());
        assertEquals(1L, result.getEvaluationDebugLog().getSegmentDebugLogs().get(0).getSegmentId());
        assertEquals("matched", result.getEvaluationDebugLog().getSegmentDebugLogs().get(0).getMessage());
    }

    @Test
    void evaluationResultIsDeserializedFromTheModelFieldNamesToo() throws Exception {
        final String json = "{\"flagId\":42,\"variantId\":7,\"segmentId\":1,\"flagSnapshotId\":596}";

        final EvaluationResult result = objectMapper.readValue(json, EvaluationResult.class);

        assertEquals(42L, result.getFlagId());
        assertEquals(7L, result.getVariantId());
        assertEquals(1L, result.getSegmentId());
        assertEquals(596L, result.getFlagSnapshotId());
    }

    @Test
    void evaluationContextIsSerializedWithTheModelFieldNames() throws Exception {
        final Map<String, Object> entityContext = Collections.singletonMap("country", "JP");
        final EvaluationContext context = EvaluationContext.builder()
                                                           .entityId("debug")
                                                           .entityType("user")
                                                           .entityContext(entityContext)
                                                           .enableDebug(true)
                                                           .flagId(42L)
                                                           .flagKey("hello-world-enabled")
                                                           .build();

        final String json = objectMapper.writeValueAsString(context);

        assertTrue(json.contains("\"entityId\":\"debug\""), json);
        assertTrue(json.contains("\"entityType\":\"user\""), json);
        assertTrue(json.contains("\"country\":\"JP\""), json);
        assertTrue(json.contains("\"enableDebug\":true"), json);
        assertTrue(json.contains("\"flagId\":42"), json);
        assertTrue(json.contains("\"flagKey\":\"hello-world-enabled\""), json);
    }

    @Test
    void evaluationContextNoArgsConstructorAndSettersWork() {
        final EvaluationContext context = new EvaluationContext();
        context.setEntityId("debug");
        context.setEntityType("user");
        context.setEntityContext(Collections.emptyMap());
        context.setEnableDebug(false);
        context.setFlagId(1L);
        context.setFlagKey("key");

        assertEquals("debug", context.getEntityId());
        assertEquals("user", context.getEntityType());
        assertEquals(Collections.emptyMap(), context.getEntityContext());
        assertEquals(Boolean.FALSE, context.getEnableDebug());
        assertEquals(1L, context.getFlagId());
        assertEquals("key", context.getFlagKey());
    }

    @Test
    void evaluationContextIsValueBased() {
        final EvaluationContext first = EvaluationContext.builder().flagKey("a").entityId("1").build();
        final EvaluationContext second = new EvaluationContext("1", null, null, null, null, "a");
        final EvaluationContext other = EvaluationContext.builder().flagKey("b").entityId("1").build();

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
        assertNotEquals(first, other);
        assertTrue(first.toString().contains("flagKey=a"));
    }

    @Test
    void evaluationResultIsValueBased() {
        final EvaluationResult first = EvaluationResult.builder().flagId(1L).variantKey("on").build();
        final EvaluationResult second = EvaluationResult.builder().flagId(1L).variantKey("on").build();
        final EvaluationResult other = EvaluationResult.builder().flagId(1L).variantKey("off").build();

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
        assertNotEquals(first, other);
        assertTrue(first.toString().contains("variantKey=on"));
    }

    @Test
    void evaluationResultNoArgsConstructorAndSettersWork() {
        final EvaluationResult result = new EvaluationResult();
        result.setFlagId(1L);
        result.setFlagKey("key");
        result.setFlagSnapshotId(2L);
        result.setSegmentId(3L);
        result.setVariantId(4L);
        result.setVariantKey("on");
        result.setVariantAttachment(Collections.singletonMap("k", "v"));
        result.setEvaluationContext(new EvaluationContext());
        result.setTimestamp(Instant.EPOCH);
        result.setEvaluationDebugLog(new EvaluationDebugLog());

        assertEquals(1L, result.getFlagId());
        assertEquals("key", result.getFlagKey());
        assertEquals(2L, result.getFlagSnapshotId());
        assertEquals(3L, result.getSegmentId());
        assertEquals(4L, result.getVariantId());
        assertEquals("on", result.getVariantKey());
        assertEquals("v", result.getVariantAttachment().get("k"));
        assertEquals(new EvaluationContext(), result.getEvaluationContext());
        assertEquals(Instant.EPOCH, result.getTimestamp());
        assertEquals(new EvaluationDebugLog(), result.getEvaluationDebugLog());
    }

    @Test
    void debugLogModelsAreValueBased() {
        final SegmentDebugLog segmentDebugLog = SegmentDebugLog.builder().segmentId(1L).message("matched").build();
        final EvaluationDebugLog debugLog = EvaluationDebugLog.builder()
                                                              .message("matched all constraints")
                                                              .segmentDebugLogs(
                                                                      Collections.singletonList(segmentDebugLog))
                                                              .build();

        assertEquals(
                new SegmentDebugLog(1L, "matched"),
                debugLog.getSegmentDebugLogs().get(0));
        assertEquals(
                new EvaluationDebugLog(Collections.singletonList(segmentDebugLog), "matched all constraints"),
                debugLog);
        assertTrue(debugLog.toString().contains("matched all constraints"));

        final SegmentDebugLog empty = new SegmentDebugLog();
        assertNull(empty.getSegmentId());
        assertNull(empty.getMessage());
        empty.setSegmentId(2L);
        empty.setMessage("not matched");
        assertEquals(2L, empty.getSegmentId());
        assertEquals("not matched", empty.getMessage());
        assertNotEquals(segmentDebugLog, empty);

        final EvaluationDebugLog emptyDebugLog = new EvaluationDebugLog();
        emptyDebugLog.setMessage("msg");
        emptyDebugLog.setSegmentDebugLogs(Collections.emptyList());
        assertEquals("msg", emptyDebugLog.getMessage());
        assertEquals(Collections.emptyList(), emptyDebugLog.getSegmentDebugLogs());
    }
}
