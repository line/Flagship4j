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
package com.linecorp.flagship4j.javaflagr.client.impl;

import com.linecorp.flagship4j.javaflagr.client.stub.FlagrEvalClientStub;
import com.linecorp.flagship4j.javaflagr.model.EvaluationContext;
import com.linecorp.flagship4j.javaflagr.model.FlagrEvalClientSettings;
import com.linecorp.flagship4j.javaflagr.model.PostEvaluationResponse;
import com.linecorp.flagship4j.javaflagr.client.FlagrEvalClient;
import com.linecorp.flagship4j.javaflagr.exception.FlagrException;

import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Slf4j
public class FlagrEvalClientImpl implements FlagrEvalClient {

    private final FlagrEvalClientSettings settings;

    private final FlagrEvalClientStub stub;

    public FlagrEvalClientImpl(FlagrEvalClientSettings settings) {
        this.settings = settings;
        this.stub = buildStub();
    }

    private FlagrEvalClientStub buildStub() {
        return new Retrofit.Builder()
                .baseUrl(settings.getBaseUrl())
                .addConverterFactory(JacksonConverterFactory.create())
                .client(buildOkHttpClient())
                .build()
                .create(FlagrEvalClientStub.class);
    }

    private OkHttpClient buildOkHttpClient() {
        final TimeUnit timeoutUnit = TimeUnit.SECONDS;

        return new OkHttpClient.Builder()
                .connectTimeout(settings.getConnectionTimeout(), timeoutUnit)
                .callTimeout(settings.getDefaultTimeout(), timeoutUnit)
                .readTimeout(settings.getReadTimeout(), timeoutUnit)
                .writeTimeout(settings.getDefaultTimeout(), timeoutUnit)
                .build();
    }

    @Override
    public PostEvaluationResponse evaluate(EvaluationContext evaluationContext) {
        return handleExceptionExecution(stub.postEvaluation(evaluationContext));
    }

    private PostEvaluationResponse handleExceptionExecution(Call<PostEvaluationResponse> call) {
        try {
            Response<PostEvaluationResponse> res = call.execute();
            if (res.isSuccessful() && Objects.nonNull(res.body())) {
                return res.body();
            } else {
                throw new FlagrException("Non-200 Response or null response. Got msg: " + res.message());
            }
        } catch (java.net.ConnectException e) {
            throw new FlagrException("Connection Fail.");
        } catch (IOException e) {
            throw new FlagrException("Unexpected Failure.");
        }
    }

}
