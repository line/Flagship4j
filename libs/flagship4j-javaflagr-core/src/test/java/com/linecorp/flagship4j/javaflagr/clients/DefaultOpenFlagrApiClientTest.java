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
package com.linecorp.flagship4j.javaflagr.clients;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.linecorp.flagship4j.javaflagr.DefaultOpenFlagr;
import com.linecorp.flagship4j.javaflagr.configs.OpenFlagrConfig;
import com.linecorp.flagship4j.javaflagr.exceptions.OpenFlagrException;
import com.linecorp.flagship4j.javaflagr.models.EvaluationContext;
import com.linecorp.flagship4j.javaflagr.models.EvaluationResult;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

class DefaultOpenFlagrApiClientTest {

    /**
     * Evaluation response of OpenFlagr 1.2.3+, which contains fields unknown to this SDK
     * ({@code flagTags}, {@code recordSource}) on top of the fields of older servers.
     */
    private static final String EVALUATION_RESPONSE_WITH_UNKNOWN_FIELDS = "{"
            + "\"evalContext\":{"
            + "\"enableDebug\":true,"
            + "\"entityID\":\"debug\","
            + "\"entityContext\":{},"
            + "\"flagKey\":\"hello-world-enabled\""
            + "},"
            + "\"evalDebugLog\":{"
            + "\"msg\":\"matched all constraints\","
            + "\"segmentDebugLogs\":[{\"segmentID\":1,\"msg\":\"matched\",\"unknownNested\":\"x\"}],"
            + "\"unknownNested\":\"x\""
            + "},"
            + "\"flagID\":42,"
            + "\"flagKey\":\"hello-world-enabled\","
            + "\"flagSnapshotID\":596,"
            + "\"flagTags\":[\"config\"],"
            + "\"recordSource\":\"evaluation\","
            + "\"segmentID\":1,"
            + "\"timestamp\":\"2026-07-30T05:00:00Z\","
            + "\"variantAttachment\":{\"enabled\":true},"
            + "\"variantID\":7,"
            + "\"variantKey\":\"on\""
            + "}";

    private MockWebServer mockWebServer;

    private OpenFlagrConfig config;

    @BeforeEach
    void setUp() throws Exception {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        config = OpenFlagrConfig.builder()
                                .endpoint(mockWebServer.url("/").toString())
                                .connectionTimeoutSeconds(3)
                                .readTimeoutSeconds(3)
                                .writeTimeoutSeconds(3)
                                .callTimeoutSeconds(3)
                                .build();
    }

    @AfterEach
    void tearDown() throws Exception {
        mockWebServer.shutdown();
    }

    @Test
    void postEvaluationToleratesUnknownResponseFields() {
        mockWebServer.enqueue(jsonResponse(EVALUATION_RESPONSE_WITH_UNKNOWN_FIELDS));

        EvaluationResult result = new DefaultOpenFlagrApiClient(config)
                .postEvaluation(evaluationContext());

        assertEquals("on", result.getVariantKey());
        assertEquals(596L, result.getFlagSnapshotId());
        assertEquals(Boolean.TRUE, result.getVariantAttachment().get("enabled"));
    }

    @Test
    void isFeatureFlagOnToleratesUnknownResponseFields() {
        mockWebServer.enqueue(jsonResponse(EVALUATION_RESPONSE_WITH_UNKNOWN_FIELDS));

        assertTrue(new DefaultOpenFlagr(config).isFeatureFlagOn("hello-world-enabled"));
    }

    @Test
    void postEvaluationSendsThePayloadToTheEvaluationEndpoint() throws Exception {
        mockWebServer.enqueue(jsonResponse(EVALUATION_RESPONSE_WITH_UNKNOWN_FIELDS));

        new DefaultOpenFlagrApiClient(config).postEvaluation(evaluationContext());

        final RecordedRequest request = mockWebServer.takeRequest();
        assertEquals("POST", request.getMethod());
        assertEquals("/api/v1/evaluation", request.getPath());
        assertEquals("application/json; charset=utf-8", request.getHeader("Content-Type"));
        assertTrue(request.getBody().readUtf8().contains("\"flagKey\":\"hello-world-enabled\""));
    }

    @Test
    void postEvaluationNormalizesATrailingSlashInTheEndpoint() throws Exception {
        mockWebServer.enqueue(jsonResponse(EVALUATION_RESPONSE_WITH_UNKNOWN_FIELDS));
        final OpenFlagrConfig configWithTrailingSlash = OpenFlagrConfig.builder()
                .endpoint(mockWebServer.url("/base/").toString())
                .connectionTimeoutSeconds(3)
                .readTimeoutSeconds(3)
                .writeTimeoutSeconds(3)
                .callTimeoutSeconds(3)
                .build();

        new DefaultOpenFlagrApiClient(configWithTrailingSlash).postEvaluation(evaluationContext());

        assertEquals("/base/api/v1/evaluation", mockWebServer.takeRequest().getPath());
    }

    @Test
    void postEvaluationFailsOnNonSuccessfulResponse() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(500));
        final DefaultOpenFlagrApiClient client = new DefaultOpenFlagrApiClient(config);
        final EvaluationContext evaluationContext = evaluationContext();

        final OpenFlagrException ex =
                assertThrows(OpenFlagrException.class, () -> client.postEvaluation(evaluationContext));

        assertEquals("failed to execute post evaluation", ex.getMessage());
    }

    @Test
    void postEvaluationFailsOnMalformedResponseBody() {
        mockWebServer.enqueue(jsonResponse("<html>not json</html>"));
        final DefaultOpenFlagrApiClient client = new DefaultOpenFlagrApiClient(config);
        final EvaluationContext evaluationContext = evaluationContext();

        assertThrows(OpenFlagrException.class, () -> client.postEvaluation(evaluationContext));
    }

    @Test
    void postEvaluationFailsWhenTheContextCannotBeSerialized() {
        final DefaultOpenFlagrApiClient client = new DefaultOpenFlagrApiClient(config);
        final EvaluationContext evaluationContext = EvaluationContext.builder()
                .flagKey("hello-world-enabled")
                .entityContext(Collections.singletonMap("exploding", new Exploding()))
                .build();

        final IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class, () -> client.postEvaluation(evaluationContext));

        assertEquals("invalidate evaluation context", ex.getMessage());
    }

    @Test
    void constructorRejectsAnEndpointWithAnUnknownProtocol() {
        final OpenFlagrConfig invalidConfig = OpenFlagrConfig.builder()
                .endpoint("flagr://localhost")
                .connectionTimeoutSeconds(3)
                .readTimeoutSeconds(3)
                .writeTimeoutSeconds(3)
                .callTimeoutSeconds(3)
                .build();

        final IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class, () -> new DefaultOpenFlagrApiClient(invalidConfig));

        assertEquals("invalidate URL: flagr://localhost", ex.getMessage());
    }

    /** A value Jackson cannot serialize, used to exercise the serialization failure branch. */
    static class Exploding {
        public String getValue() {
            throw new IllegalStateException("cannot be serialized");
        }
    }

    private static EvaluationContext evaluationContext() {
        return EvaluationContext.builder()
                                .flagKey("hello-world-enabled")
                                .entityId("debug")
                                .enableDebug(true)
                                .build();
    }

    private static MockResponse jsonResponse(String body) {
        return new MockResponse()
                .setHeader("Content-Type", "application/json")
                .setBody(body);
    }

}
