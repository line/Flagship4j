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
package com.linecorp.flagship4j.openfeature.springframework.boot.autoconfigure;

import java.time.Duration;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;

import com.linecorp.flagship4j.javaflagr.DefaultOpenFlagr;
import com.linecorp.flagship4j.javaflagr.configs.OpenFlagrConfig;
import com.linecorp.flagship4j.openfeature.OpenFlagrProvider;

import dev.openfeature.sdk.Client;
import dev.openfeature.sdk.FeatureProvider;
import dev.openfeature.sdk.OpenFeatureAPI;
import lombok.RequiredArgsConstructor;

@AutoConfiguration
@ConditionalOnClass(OpenFeatureAPI.class)
@EnableConfigurationProperties(OpenFeatureProperties.class)
@RequiredArgsConstructor
public class OpenFeatureAutoConfiguration {

    private final OpenFeatureProperties openFlagrProperties;

    @Bean
    @Order(1)
    @ConditionalOnMissingBean(FeatureProvider.class)
    public FeatureProvider flagrFeatureProvider() {
        OpenFlagrConfig config = buildOpenFlagrConfig(openFlagrProperties);
        return new OpenFlagrProvider(new DefaultOpenFlagr(config));
    }

    private OpenFlagrConfig buildOpenFlagrConfig(OpenFeatureProperties openFlagrProperties) {
        return OpenFlagrConfig.builder()
                              .endpoint(openFlagrProperties.getBaseUrl())
                              .callTimeout(Duration.ofSeconds(openFlagrProperties.getCallTimeout()))
                              .connectionTimeout(Duration.ofSeconds(openFlagrProperties.getConnectionTimeout()))
                              .readTimeout(Duration.ofSeconds(openFlagrProperties.getReadTimeout()))
                              .writeTimeout(Duration.ofSeconds(openFlagrProperties.getWriteTimeout()))
                              .build();
    }

    @Bean
    @Order(2)
    @ConditionalOnMissingBean(OpenFeatureAPI.class)
    public OpenFeatureAPI openFeatureAPI(FeatureProvider provider) {
        OpenFeatureAPI openFeatureAPI = OpenFeatureAPI.getInstance();
        openFeatureAPI.setProvider(provider);
        return openFeatureAPI;
    }

    @Bean
    @Order(3)
    @ConditionalOnMissingBean(Client.class)
    public Client defaultOpenFeatureAPIClient(OpenFeatureAPI openFeatureAPI) {
        return openFeatureAPI.getClient();
    }

}
