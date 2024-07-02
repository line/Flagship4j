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
package com.linecorp.flagship4j.javaflagr.client;

import com.linecorp.flagship4j.javaflagr.annotation.TestWebClient;
import com.linecorp.flagship4j.javaflagr.fixture.PostEvaluationResponseFixture;
import com.linecorp.flagship4j.javaflagr.base.RestApiClientTestBase;
import com.linecorp.flagship4j.javaflagr.config.FlagrEvalClientConfiguration;
import com.linecorp.flagship4j.javaflagr.model.EvaluationContext;
import com.linecorp.flagship4j.javaflagr.model.PostEvaluationResponse;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import static com.linecorp.flagship4j.javaflagr.fixture.PostEvaluationResponseFixture.mockSuccessPostEvaluationResponse;
import static org.assertj.core.api.Assertions.assertThat;

@TestWebClient(components = { FlagrEvalClientConfiguration.class})
public class FlagrEvalClientTest extends RestApiClientTestBase {

    @Autowired
    private FlagrEvalClient flagrEvalClient;

    @Test
    public void testPostEvaluationSuccess() throws Exception {
        PostEvaluationResponse givenResponse = PostEvaluationResponseFixture.mockSuccessPostEvaluationResponse();
        EvaluationContext givenEvaluationContext = new EvaluationContext();
        givenEvaluationContext.setFlagKey("exist_feature_flag");

        String expectedRequestMethod = HttpMethod.POST.toString();

        mockWebServer.enqueue(new MockResponse()
                .setBody(objectMapper.writeValueAsString(givenResponse)));

        PostEvaluationResponse response = flagrEvalClient.evaluate(givenEvaluationContext);
        RecordedRequest request = mockWebServer.takeRequest();

        assertThat(request.getMethod())
                .as("Check if request method is %s.", expectedRequestMethod)
                .isEqualTo(expectedRequestMethod);

        assertThat(response.getVariantKey())
                .as("Check if returned variant is not null.")
                .isNotNull();
    }
}
