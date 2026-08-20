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
package com.linecorp.flagship4j.openfeature.springframework.boot.autoconfigure;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.linecorp.flagship4j.openfeature.OpenFlagrProvider;

import dev.openfeature.sdk.Client;
import dev.openfeature.sdk.EvaluationContext;
import dev.openfeature.sdk.FeatureProvider;
import dev.openfeature.sdk.Metadata;
import dev.openfeature.sdk.OpenFeatureAPI;
import dev.openfeature.sdk.ProviderEvaluation;
import dev.openfeature.sdk.Value;

class OpenFeatureAutoConfigurationTest {

    private static final String BASE_URL_PROPERTY = "flagship4j.toggle.flagr.base-url=http://localhost:18000";

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(OpenFeatureAutoConfiguration.class));

    @Test
    void registersTheOpenFlagrProviderTheApiAndAClient() {
        contextRunner.withPropertyValues(BASE_URL_PROPERTY).run(context -> {
            assertThat(context).hasSingleBean(FeatureProvider.class)
                               .hasSingleBean(OpenFeatureAPI.class)
                               .hasSingleBean(Client.class);
            assertThat(context.getBean(FeatureProvider.class)).isInstanceOf(OpenFlagrProvider.class);
            assertThat(context.getBean(FeatureProvider.class).getMetadata().getName())
                    .isEqualTo("OpenFlagr Provider");
        });
    }

    @Test
    void bindsTheFlagrProperties() {
        contextRunner.withPropertyValues(
                BASE_URL_PROPERTY,
                "flagship4j.toggle.flagr.connection-timeout=1",
                "flagship4j.toggle.flagr.read-timeout=2",
                "flagship4j.toggle.flagr.call-timeout=3",
                "flagship4j.toggle.flagr.write-timeout=4").run(context -> {
                    final OpenFeatureProperties properties = context.getBean(OpenFeatureProperties.class);

                    assertThat(properties.getBaseUrl()).isEqualTo("http://localhost:18000");
                    assertThat(properties.getConnectionTimeout()).isEqualTo(1);
                    assertThat(properties.getReadTimeout()).isEqualTo(2);
                    assertThat(properties.getCallTimeout()).isEqualTo(3);
                    assertThat(properties.getWriteTimeout()).isEqualTo(4);
                });
    }

    @Test
    void backsOffWhenTheApplicationDeclaresItsOwnProvider() {
        contextRunner.withPropertyValues(BASE_URL_PROPERTY)
                     .withUserConfiguration(CustomProviderConfiguration.class)
                     .run(context -> {
                         assertThat(context).hasSingleBean(FeatureProvider.class);
                         assertThat(context.getBean(FeatureProvider.class)).isInstanceOf(NoOpProvider.class);
                     });
    }

    @Configuration(proxyBeanMethods = false)
    static class CustomProviderConfiguration {

        @Bean
        FeatureProvider customFeatureProvider() {
            return new NoOpProvider();
        }
    }

    /** A provider that is only used to verify the {@code @ConditionalOnMissingBean} back off. */
    static class NoOpProvider implements FeatureProvider {

        @Override
        public Metadata getMetadata() {
            return () -> "No Op Provider";
        }

        @Override
        public ProviderEvaluation<Boolean> getBooleanEvaluation(
                String key, Boolean defaultValue, EvaluationContext ctx) {
            return ProviderEvaluation.<Boolean>builder().value(defaultValue).build();
        }

        @Override
        public ProviderEvaluation<String> getStringEvaluation(
                String key, String defaultValue, EvaluationContext ctx) {
            return ProviderEvaluation.<String>builder().value(defaultValue).build();
        }

        @Override
        public ProviderEvaluation<Integer> getIntegerEvaluation(
                String key, Integer defaultValue, EvaluationContext ctx) {
            return ProviderEvaluation.<Integer>builder().value(defaultValue).build();
        }

        @Override
        public ProviderEvaluation<Double> getDoubleEvaluation(
                String key, Double defaultValue, EvaluationContext ctx) {
            return ProviderEvaluation.<Double>builder().value(defaultValue).build();
        }

        @Override
        public ProviderEvaluation<Value> getObjectEvaluation(
                String key, Value defaultValue, EvaluationContext ctx) {
            return ProviderEvaluation.<Value>builder().value(defaultValue).build();
        }
    }
}
