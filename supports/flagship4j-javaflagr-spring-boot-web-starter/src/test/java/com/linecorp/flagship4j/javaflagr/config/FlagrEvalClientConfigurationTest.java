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
package com.linecorp.flagship4j.javaflagr.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.lang.reflect.Field;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;

import com.linecorp.flagship4j.javaflagr.aspect.ControllerFeatureToggleAspect;
import com.linecorp.flagship4j.javaflagr.bean.BeanLocator;
import com.linecorp.flagship4j.javaflagr.client.FlagrEvalClient;
import com.linecorp.flagship4j.javaflagr.config.properties.Flagship4jToggleFlagrProperties;
import com.linecorp.flagship4j.javaflagr.service.FlagrEvalService;

/**
 * Instantiates the {@code @Bean} factory methods directly. Creating a {@link BeanLocator} writes to
 * a static field, so its original value is saved and restored around this class.
 */
class FlagrEvalClientConfigurationTest {

    private static ApplicationContext originalApplicationContext;

    private final FlagrEvalClientConfiguration configuration = new FlagrEvalClientConfiguration();

    @BeforeAll
    static void saveOriginalApplicationContext() throws Exception {
        originalApplicationContext = (ApplicationContext) applicationContextField().get(null);
    }

    @AfterAll
    static void restoreOriginalApplicationContext() throws Exception {
        applicationContextField().set(null, originalApplicationContext);
    }

    @Test
    void createsAFlagrEvalClientFromTheProperties() {
        final Flagship4jToggleFlagrProperties properties = new Flagship4jToggleFlagrProperties();
        properties.setBaseUrl("http://localhost:18000");

        final FlagrEvalClient client = configuration.flagrEvalClient(properties);

        assertThat(client).isNotNull();
    }

    @Test
    void createsAFlagrEvalService() {
        final FlagrEvalService service = configuration.flagrEvalService(mock(FlagrEvalClient.class));

        assertThat(service).isNotNull();
    }

    @Test
    void createsAControllerFeatureToggleAspect() {
        final ControllerFeatureToggleAspect aspect =
                configuration.controllerFeatureToggleAspect(mock(FlagrEvalService.class));

        assertThat(aspect).isNotNull();
    }

    @Test
    void createsABeanLocatorBackedByTheApplicationContext() {
        final ApplicationContext applicationContext = mock(ApplicationContext.class);

        final BeanLocator beanLocator = configuration.beanLocator(applicationContext);

        assertThat(beanLocator).isNotNull();
        assertThat(BeanLocator.isInitialized()).isTrue();
    }

    private static Field applicationContextField() throws Exception {
        final Field field = BeanLocator.class.getDeclaredField("applicationContext");
        field.setAccessible(true);
        return field;
    }
}
