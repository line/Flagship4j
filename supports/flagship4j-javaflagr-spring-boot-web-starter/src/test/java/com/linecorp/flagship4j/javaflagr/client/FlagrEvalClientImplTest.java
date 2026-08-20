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
package com.linecorp.flagship4j.javaflagr.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.linecorp.flagship4j.javaflagr.client.impl.FlagrEvalClientImpl;
import com.linecorp.flagship4j.javaflagr.exception.FlagrException;
import com.linecorp.flagship4j.javaflagr.fixture.PostEvaluationResponseFixture;
import com.linecorp.flagship4j.javaflagr.model.EvaluationContext;
import com.linecorp.flagship4j.javaflagr.model.FlagrEvalClientSettings;
import com.linecorp.flagship4j.javaflagr.model.PostEvaluationResponse;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.SocketPolicy;

/**
 * Exercises {@link FlagrEvalClientImpl} directly, without the Spring context, so that the failure
 * branches of the Retrofit call can be driven from a mock web server.
 */
class FlagrEvalClientImplTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private MockWebServer mockWebServer;

    private FlagrEvalClient flagrEvalClient;

    @BeforeEach
    void startMockWebServer() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        flagrEvalClient = new FlagrEvalClientImpl(settingsFor(mockWebServer.url("/").toString()));
    }

    @AfterEach
    void shutdownMockWebServer() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void evaluateReturnsTheDeserializedResponse() throws Exception {
        final PostEvaluationResponse givenResponse =
                PostEvaluationResponseFixture.mockSuccessPostEvaluationResponse();
        mockWebServer.enqueue(new MockResponse().setBody(objectMapper.writeValueAsString(givenResponse)));

        final PostEvaluationResponse response = flagrEvalClient.evaluate(evaluationContext());

        assertThat(response.getFlagKey()).isEqualTo("exist_feature_flag");
        assertThat(response.getVariantKey()).isEqualTo("on");
        assertThat(mockWebServer.takeRequest().getPath()).isEqualTo("/api/v1/evaluation");
    }

    @Test
    void evaluateFailsOnANonSuccessfulResponse() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(500));

        assertThatThrownBy(() -> flagrEvalClient.evaluate(evaluationContext()))
                .isInstanceOf(FlagrException.class)
                .hasMessageStartingWith("Non-200 Response or null response.");
    }

    @Test
    void evaluateFailsOnAnEmptyResponseBody() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(204));

        assertThatThrownBy(() -> flagrEvalClient.evaluate(evaluationContext()))
                .isInstanceOf(FlagrException.class)
                .hasMessageStartingWith("Non-200 Response or null response.");
    }

    @Test
    void evaluateFailsWhenTheConnectionIsRefused() {
        mockWebServer.enqueue(new MockResponse().setSocketPolicy(SocketPolicy.DISCONNECT_AT_START));

        assertThatThrownBy(() -> flagrEvalClient.evaluate(evaluationContext()))
                .isInstanceOf(FlagrException.class)
                .hasMessage("Connection Fail.");
    }

    @Test
    void evaluateFailsWhenTheResponseIsTruncated() throws Exception {
        final PostEvaluationResponse givenResponse =
                PostEvaluationResponseFixture.mockSuccessPostEvaluationResponse();
        mockWebServer.enqueue(new MockResponse()
                                      .setBody(objectMapper.writeValueAsString(givenResponse))
                                      .setSocketPolicy(SocketPolicy.DISCONNECT_DURING_RESPONSE_BODY));

        assertThatThrownBy(() -> flagrEvalClient.evaluate(evaluationContext()))
                .isInstanceOf(FlagrException.class)
                .hasMessage("Unexpected Failure.");
    }

    private static FlagrEvalClientSettings settingsFor(String baseUrl) {
        return FlagrEvalClientSettings.builder()
                                      .baseUrl(baseUrl)
                                      .connectionTimeout(3)
                                      .readTimeout(3)
                                      .defaultTimeout(3)
                                      .retry(2)
                                      .build();
    }

    private static EvaluationContext evaluationContext() {
        final EvaluationContext evaluationContext = new EvaluationContext();
        evaluationContext.setFlagKey("exist_feature_flag");
        evaluationContext.setEnableDebug(true);
        return evaluationContext;
    }
}
