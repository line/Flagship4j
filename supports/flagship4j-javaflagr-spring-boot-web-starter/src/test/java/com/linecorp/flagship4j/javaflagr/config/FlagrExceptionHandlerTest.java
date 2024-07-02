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
package com.linecorp.flagship4j.javaflagr.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import com.linecorp.flagship4j.javaflagr.exception.FlagrApiNotFoundException;
import com.linecorp.flagship4j.javaflagr.exception.FlagrErrorResponse;
import com.linecorp.flagship4j.javaflagr.exception.FlagrException;

class FlagrExceptionHandlerTest {

    private FlagrExceptionHandler flagrExceptionHandler;

    @BeforeEach
    void setUp() {
        flagrExceptionHandler = new FlagrExceptionHandler();
    }

    @Test
    void handleFlagrException() {
        final ResponseEntity<FlagrErrorResponse>
                result = flagrExceptionHandler.handleFlagrException(new FlagrException("flagr exception"));

        assertThat(result.getStatusCodeValue()).isEqualTo(500);
        assertThat(result.getBody())
                .extracting(FlagrErrorResponse::getStatus, FlagrErrorResponse::getMessage)
                .containsExactly(500, "flagr exception");
    }

    @Test
    void handleFlagrApiNotFoundException() {
        final ResponseEntity<FlagrErrorResponse>
                result = flagrExceptionHandler.handleFlagrResponseStatusException(new FlagrApiNotFoundException());

        assertThat(result.getStatusCodeValue()).isEqualTo(404);
        assertThat(result.getBody())
                .extracting(FlagrErrorResponse::getStatus, FlagrErrorResponse::getMessage)
                .containsExactly(404, "Not Found");
    }
}
