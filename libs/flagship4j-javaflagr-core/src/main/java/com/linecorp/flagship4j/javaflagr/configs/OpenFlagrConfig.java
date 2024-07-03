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
package com.linecorp.flagship4j.javaflagr.configs;

import java.net.URI;
import java.time.Duration;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OpenFlagrConfig {

    private URI endpoint;

    private Duration connectionTimeout;

    private Duration readTimeout;

    private Duration writeTimeout;

    private Duration callTimeout;

    public static class OpenFlagrConfigBuilder {

        private URI endpoint;

        private Duration connectionTimeout;

        private Duration readTimeout;

        private Duration writeTimeout;

        private Duration callTimeout;

        public OpenFlagrConfigBuilder endpoint(String endpoint) {
            this.endpoint = URI.create(endpoint);
            return this;
        }

        public OpenFlagrConfigBuilder endpoint(URI endpoint) {
            this.endpoint = endpoint;
            return this;
        }

        public OpenFlagrConfigBuilder connectionTimeoutMs(long connectionTimeoutMs) {
            this.connectionTimeout = Duration.ofMillis(connectionTimeoutMs);
            return this;
        }

        public OpenFlagrConfigBuilder connectionTimeoutSeconds(long connectionTimeoutSeconds) {
            this.connectionTimeout = Duration.ofSeconds(connectionTimeoutSeconds);
            return this;
        }

        public OpenFlagrConfigBuilder readTimeoutMs(long readTimeoutMs) {
            this.readTimeout = Duration.ofMillis(readTimeoutMs);
            return this;
        }

        public OpenFlagrConfigBuilder readTimeoutSeconds(long readTimeoutSeconds) {
            this.readTimeout = Duration.ofSeconds(readTimeoutSeconds);
            return this;
        }

        public OpenFlagrConfigBuilder writeTimeoutMs(long writeTimeoutMs) {
            this.writeTimeout = Duration.ofMillis(writeTimeoutMs);
            return this;
        }

        public OpenFlagrConfigBuilder writeTimeoutSeconds(long writeTimeoutSeconds) {
            this.writeTimeout = Duration.ofSeconds(writeTimeoutSeconds);
            return this;
        }

        public OpenFlagrConfigBuilder callTimeoutMs(long callTimeoutMs) {
            this.callTimeout = Duration.ofMillis(callTimeoutMs);
            return this;
        }

        public OpenFlagrConfigBuilder callTimeoutSeconds(long callTimeoutSeconds) {
            this.callTimeout = Duration.ofSeconds(callTimeoutSeconds);
            return this;
        }

    }

}