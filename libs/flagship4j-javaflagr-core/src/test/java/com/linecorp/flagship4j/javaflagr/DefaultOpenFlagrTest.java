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
package com.linecorp.flagship4j.javaflagr;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import com.linecorp.flagship4j.javaflagr.configs.OpenFlagrConfig;
import com.linecorp.flagship4j.javaflagr.models.EvaluationContext;
import com.linecorp.flagship4j.javaflagr.models.EvaluationResult;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

class DefaultOpenFlagrTest {

    private static final String FLAG_KEY = "hello-world-enabled";

    private MockWebServer mockWebServer;

    private DefaultOpenFlagr openFlagr;

    @BeforeEach
    void setUp() throws Exception {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        openFlagr = new DefaultOpenFlagr(OpenFlagrConfig.builder()
                                                        .endpoint(mockWebServer.url("/").toString())
                                                        .connectionTimeoutSeconds(3)
                                                        .readTimeoutSeconds(3)
                                                        .writeTimeoutSeconds(3)
                                                        .callTimeoutSeconds(3)
                                                        .build());
    }

    @AfterEach
    void tearDown() throws Exception {
        mockWebServer.shutdown();
    }

    // ------------------------------------------------------------------ isFeatureFlagOn

    @ParameterizedTest
    @CsvSource({ "on,true", "true,true", "1,true", "off,false", "false,false", "0,false" })
    void isFeatureFlagOnMapsEveryEffectiveVariantAlias(String variantKey, boolean expected) {
        enqueueEvaluation(variantKey);

        assertEquals(expected, openFlagr.isFeatureFlagOn(FLAG_KEY));
    }

    @Test
    void isFeatureFlagOnFallsBackToFalseWhenVariantIsNotAnEffectiveVariant() {
        enqueueEvaluation("control");

        assertFalse(openFlagr.isFeatureFlagOn(FLAG_KEY));
    }

    @Test
    void isFeatureFlagOnFallsBackToFalseWhenFlagIsUnknown() {
        mockWebServer.enqueue(jsonResponse("{\"flagKey\":\"" + FLAG_KEY + "\"}"));

        assertFalse(openFlagr.isFeatureFlagOn(FLAG_KEY));
    }

    @Test
    void isFeatureFlagOnFallsBackToFalseWhenNoVariantIsAssigned() {
        mockWebServer.enqueue(jsonResponse("{\"flagID\":42,\"flagKey\":\"" + FLAG_KEY + "\"}"));

        assertFalse(openFlagr.isFeatureFlagOn(FLAG_KEY));
    }

    @Test
    void isFeatureFlagOnFallsBackToFalseOnServerError() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(500));

        assertFalse(openFlagr.isFeatureFlagOn(FLAG_KEY));
    }

    @Test
    void isFeatureFlagOnFallsBackToFalseOnMalformedResponseBody() {
        mockWebServer.enqueue(jsonResponse("not json at all"));

        assertFalse(openFlagr.isFeatureFlagOn(FLAG_KEY));
    }

    @Test
    void isFeatureFlagOnFallsBackToFalseWhenTheContextCannotBeSerialized() {
        assertFalse(openFlagr.isFeatureFlagOn(FLAG_KEY, unserializableEntityContext()));
        assertEquals(0, mockWebServer.getRequestCount());
    }

    @Test
    void isFeatureFlagOnSendsTheEntityId() throws Exception {
        enqueueEvaluation("on");

        assertTrue(openFlagr.isFeatureFlagOn(FLAG_KEY, "user-1"));

        final String body = takeRequestBody();
        assertTrue(body.contains("\"entityId\":\"user-1\""), body);
        assertTrue(body.contains("\"flagKey\":\"" + FLAG_KEY + "\""), body);
        assertTrue(body.contains("\"enableDebug\":true"), body);
    }

    @Test
    void isFeatureFlagOnSendsTheEntityContext() throws Exception {
        enqueueEvaluation("on");

        assertTrue(openFlagr.isFeatureFlagOn(FLAG_KEY, Collections.singletonMap("country", "JP")));

        final String body = takeRequestBody();
        assertTrue(body.contains("\"country\":\"JP\""), body);
    }

    @Test
    void isFeatureFlagOnSendsBothEntityIdAndEntityContext() throws Exception {
        enqueueEvaluation("on");

        assertTrue(openFlagr.isFeatureFlagOn(FLAG_KEY, "user-1", Collections.singletonMap("country", "JP")));

        final String body = takeRequestBody();
        assertTrue(body.contains("\"entityId\":\"user-1\""), body);
        assertTrue(body.contains("\"country\":\"JP\""), body);
    }

    @Test
    void isFeatureFlagOnAcceptsAnExplicitEvaluationContext() throws Exception {
        enqueueEvaluation("on");

        assertTrue(openFlagr.isFeatureFlagOn(EvaluationContext.builder()
                                                              .flagKey(FLAG_KEY)
                                                              .entityType("user")
                                                              .enableDebug(false)
                                                              .build()));

        final String body = takeRequestBody();
        assertTrue(body.contains("\"entityType\":\"user\""), body);
        assertTrue(body.contains("\"enableDebug\":false"), body);
    }

    // ------------------------------------------------------------------ getVariantKey

    @Test
    void getVariantKeyReturnsTheVariantOfTheFlag() {
        enqueueEvaluation("control");

        assertEquals(Optional.of("control"), openFlagr.getVariantKey(FLAG_KEY));
    }

    @Test
    void getVariantKeyByEntityIdSendsTheEntityId() throws Exception {
        enqueueEvaluation("treatment");

        assertEquals(Optional.of("treatment"), openFlagr.getVariantKey(FLAG_KEY, "user-1"));
        assertTrue(takeRequestBody().contains("\"entityId\":\"user-1\""));
    }

    @Test
    void getVariantKeyByEntityContextSendsTheEntityContext() throws Exception {
        enqueueEvaluation("treatment");

        assertEquals(
                Optional.of("treatment"),
                openFlagr.getVariantKey(FLAG_KEY, Collections.singletonMap("country", "JP")));
        assertTrue(takeRequestBody().contains("\"country\":\"JP\""));
    }

    @Test
    void getVariantKeyByEntityIdAndContextSendsBoth() throws Exception {
        enqueueEvaluation("treatment");

        assertEquals(
                Optional.of("treatment"),
                openFlagr.getVariantKey(FLAG_KEY, "user-1", Collections.singletonMap("country", "JP")));

        final String body = takeRequestBody();
        assertTrue(body.contains("\"entityId\":\"user-1\""), body);
        assertTrue(body.contains("\"country\":\"JP\""), body);
    }

    @Test
    void getVariantKeyAcceptsAnExplicitEvaluationContext() {
        enqueueEvaluation("treatment");

        assertEquals(
                Optional.of("treatment"),
                openFlagr.getVariantKey(EvaluationContext.builder().flagKey(FLAG_KEY).build()));
    }

    @Test
    void getVariantKeyIsEmptyOnServerError() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(503));

        assertEquals(Optional.empty(), openFlagr.getVariantKey(FLAG_KEY));
    }

    // ------------------------------------------------------------------ getVariantAttachment

    @Test
    void getVariantAttachmentReturnsTheAttachmentOfTheAssignedVariant() {
        enqueueEvaluation("on");

        final Optional<Map<String, Object>> attachment = openFlagr.getVariantAttachment(FLAG_KEY);

        assertTrue(attachment.isPresent());
        assertEquals(Boolean.TRUE, attachment.get().get("enabled"));
    }

    @Test
    void getVariantAttachmentByEntityIdSendsTheEntityId() throws Exception {
        enqueueEvaluation("on");

        assertTrue(openFlagr.getVariantAttachment(FLAG_KEY, "user-1").isPresent());
        assertTrue(takeRequestBody().contains("\"entityId\":\"user-1\""));
    }

    @Test
    void getVariantAttachmentByEntityContextSendsTheEntityContext() throws Exception {
        enqueueEvaluation("on");

        assertTrue(openFlagr.getVariantAttachment(FLAG_KEY, Collections.singletonMap("country", "JP")).isPresent());
        assertTrue(takeRequestBody().contains("\"country\":\"JP\""));
    }

    @Test
    void getVariantAttachmentByEntityIdAndContextSendsBoth() throws Exception {
        enqueueEvaluation("on");

        assertTrue(openFlagr
                           .getVariantAttachment(FLAG_KEY, "user-1", Collections.singletonMap("country", "JP"))
                           .isPresent());

        final String body = takeRequestBody();
        assertTrue(body.contains("\"entityId\":\"user-1\""), body);
        assertTrue(body.contains("\"country\":\"JP\""), body);
    }

    @Test
    void getVariantAttachmentAcceptsAnExplicitEvaluationContext() {
        enqueueEvaluation("on");

        assertTrue(openFlagr
                           .getVariantAttachment(EvaluationContext.builder().flagKey(FLAG_KEY).build())
                           .isPresent());
    }

    @Test
    void getVariantAttachmentIsEmptyWhenTheVariantHasNoAttachment() {
        mockWebServer.enqueue(jsonResponse(
                "{\"flagID\":42,\"flagKey\":\"" + FLAG_KEY + "\",\"variantKey\":\"on\"}"));

        assertEquals(Optional.empty(), openFlagr.getVariantAttachment(FLAG_KEY));
    }

    @Test
    void getVariantAttachmentIsEmptyOnServerError() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(500));

        assertEquals(Optional.empty(), openFlagr.getVariantAttachment(FLAG_KEY));
    }

    // ------------------------------------------------------------------ evaluate

    @Test
    void evaluateReturnsTheWholeEvaluationResult() {
        enqueueEvaluation("on");

        final Optional<EvaluationResult> result =
                openFlagr.evaluate(EvaluationContext.builder().flagKey(FLAG_KEY).enableDebug(true).build());

        assertTrue(result.isPresent());
        assertEquals(42L, result.get().getFlagId());
        assertEquals(FLAG_KEY, result.get().getFlagKey());
        assertEquals("on", result.get().getVariantKey());
        assertEquals("matched all constraints", result.get().getEvaluationDebugLog().getMessage());
    }

    @Test
    void evaluateIsEmptyOnServerError() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(500));

        assertEquals(
                Optional.empty(),
                openFlagr.evaluate(EvaluationContext.builder().flagKey(FLAG_KEY).build()));
    }

    // ------------------------------------------------------------------ helpers

    private void enqueueEvaluation(String variantKey) {
        mockWebServer.enqueue(jsonResponse("{"
                                           + "\"flagID\":42,"
                                           + "\"flagKey\":\"" + FLAG_KEY + "\","
                                           + "\"flagSnapshotID\":596,"
                                           + "\"segmentID\":1,"
                                           + "\"variantID\":7,"
                                           + "\"variantKey\":\"" + variantKey + "\","
                                           + "\"variantAttachment\":{\"enabled\":true},"
                                           + "\"timestamp\":\"2026-07-30T05:00:00Z\","
                                           + "\"evalDebugLog\":{\"msg\":\"matched all constraints\"}"
                                           + "}"));
    }

    private String takeRequestBody() throws InterruptedException {
        final RecordedRequest request = mockWebServer.takeRequest();
        assertEquals("POST", request.getMethod());
        assertEquals("/api/v1/evaluation", request.getPath());
        return request.getBody().readUtf8();
    }

    private static Map<String, Object> unserializableEntityContext() {
        return Collections.singletonMap("exploding", new Exploding());
    }

    /** A value Jackson cannot serialize, used to exercise the serialization failure branch. */
    static class Exploding {
        public String getValue() {
            throw new IllegalStateException("cannot be serialized");
        }
    }

    private static MockResponse jsonResponse(String body) {
        return new MockResponse()
                .setHeader("Content-Type", "application/json")
                .setBody(body);
    }
}
