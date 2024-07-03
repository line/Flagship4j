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
package com.linecorp.flagship4j.javaflagr.config;

import com.linecorp.flagship4j.javaflagr.bean.BeanLocator;
import com.linecorp.flagship4j.javaflagr.config.properties.Flagship4jToggleFlagrProperties;
import com.linecorp.flagship4j.javaflagr.model.FlagrEvalClientSettings;
import com.linecorp.flagship4j.javaflagr.service.FlagrEvalService;
import com.linecorp.flagship4j.javaflagr.aspect.ControllerFeatureToggleAspect;
import com.linecorp.flagship4j.javaflagr.client.FlagrEvalClient;
import com.linecorp.flagship4j.javaflagr.client.impl.FlagrEvalClientImpl;
import com.linecorp.flagship4j.javaflagr.service.impl.FlagrEvalServiceImpl;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(Flagship4jToggleFlagrProperties.class)
public class FlagrEvalClientConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public FlagrEvalClient flagrEvalClient(Flagship4jToggleFlagrProperties properties) {
        final FlagrEvalClientSettings settings = FlagrEvalClientSettings.builder()
                                                                        .baseUrl(properties.getBaseUrl())
                                                                        .readTimeout(properties.getReadTimeout())
                                                                        .defaultTimeout(properties.getDefaultTimeout())
                                                                        .connectionTimeout(properties.getConnectionTimeout())
                                                                        .retry(properties.getRetry())
                                                                        .build();
        return new FlagrEvalClientImpl(settings);
    }

    @Bean
    @ConditionalOnMissingBean
    public FlagrEvalService flagrEvalService(FlagrEvalClient flagrEvalClient) {
        return new FlagrEvalServiceImpl(flagrEvalClient);
    }

    @Bean
    @ConditionalOnMissingBean
    public ControllerFeatureToggleAspect controllerFeatureToggleAspect(FlagrEvalService flagrEvalService) {
        return new ControllerFeatureToggleAspect(flagrEvalService);
    }

    @Bean
    @ConditionalOnMissingBean
    public BeanLocator beanLocator(ApplicationContext applicationContext) {
        return new BeanLocator(applicationContext);
    }

}
