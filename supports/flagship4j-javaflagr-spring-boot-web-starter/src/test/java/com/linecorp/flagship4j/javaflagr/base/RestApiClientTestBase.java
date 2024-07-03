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
package com.linecorp.flagship4j.javaflagr.base;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.linecorp.flagship4j.javaflagr.model.Context;
import com.linecorp.flagship4j.javaflagr.model.ContextHolder;

import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;
import java.util.Optional;

public abstract class RestApiClientTestBase {

    protected ObjectMapper objectMapper = new ObjectMapper();

    protected MockWebServer mockWebServer;

    protected Integer mockWebServerPort;

    protected RestApiClientTestBase() {
        this(8080);
    }

    protected RestApiClientTestBase(Integer mockWebServerPort) {
        this.mockWebServerPort = mockWebServerPort;
    }

    @BeforeEach
    private void startMockWebServer() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start(mockWebServerPort);
    }

    @AfterEach
    private void shutdownMockWebServer() throws IOException {
        mockWebServer.shutdown();
    }

    protected Context getLocalTestContext() {
        Optional<Context> optionalContextHolder = ContextHolder.get();
        if (optionalContextHolder.isPresent()) {
            return optionalContextHolder.get();
        } else {
            throw new RuntimeException("Value is not present");
        }
    }

    protected String getLocalTestServiceId() {
        return getLocalTestContext().getServiceId();
    }

}
