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
package com.linecorp.flagship4j.javaflagr.clients;

import java.net.MalformedURLException;
import java.net.URI;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import com.linecorp.flagship4j.javaflagr.configs.OpenFlagrConfig;
import com.linecorp.flagship4j.javaflagr.exceptions.OpenFlagrException;
import com.linecorp.flagship4j.javaflagr.models.EvaluationContext;
import com.linecorp.flagship4j.javaflagr.models.EvaluationResult;

import lombok.extern.slf4j.Slf4j;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Slf4j
public class DefaultOpenFlagrApiClient implements OpenFlagrApiClient {

    private static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");

    private static final String postEvaluationUrlPath = "/api/v1/evaluation";

    private final ObjectMapper objectMapper;

    private final OkHttpClient client;

    private HttpUrl postEvaluationUrl;

    public DefaultOpenFlagrApiClient(OpenFlagrConfig openFlagrConfig) {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(openFlagrConfig.getConnectionTimeout())
                .readTimeout(openFlagrConfig.getReadTimeout())
                .writeTimeout(openFlagrConfig.getWriteTimeout())
                .callTimeout(openFlagrConfig.getCallTimeout())
                .build();
        this.objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

        buildUrls(openFlagrConfig.getEndpoint());
    }

    @Override
    @SuppressWarnings("deprecation")
    public EvaluationResult postEvaluation(EvaluationContext evaluationContext) {
        try {
            String evaluationContextJsonString = objectMapper.writeValueAsString(evaluationContext);

            // use deprecated method for Spring boot 2(okhttp 3.14.9) compatibility
            RequestBody body = RequestBody.create(MEDIA_TYPE_JSON, evaluationContextJsonString);
            log.debug("Request Body = {}", evaluationContextJsonString);
            Request request = new Request.Builder().url(postEvaluationUrl).post(body).build();
            
            EvaluationResult result = postEvaluation(request);
            log.debug("API EvaluationResult response = {}", result);
            return result;
        } catch (JsonProcessingException ex) {
            throw new IllegalArgumentException("invalidate evaluation context", ex);
        }
    }

    private EvaluationResult postEvaluation(Request request) {
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new OpenFlagrException("response from post evaluation execution is faliure");
            }
            return objectMapper.readValue(response.body().bytes(), EvaluationResult.class);
        } catch (Exception ex) {
            throw new OpenFlagrException("failed to execute post evaluation", ex);
        }
    }

    private void buildUrls(URI endpoint) {
        try {
            this.postEvaluationUrl = HttpUrl.get(
                    URI.create(endpoint.toString() + postEvaluationUrlPath).normalize().toURL());
        } catch (MalformedURLException ex) {
            throw new IllegalArgumentException(
                    String.format("invalidate URL: %s", endpoint), ex);
        }
    }

}
