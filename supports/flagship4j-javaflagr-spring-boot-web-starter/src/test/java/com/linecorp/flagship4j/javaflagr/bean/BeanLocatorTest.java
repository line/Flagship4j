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
package com.linecorp.flagship4j.javaflagr.bean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;

import com.linecorp.flagship4j.javaflagr.model.Context;

/**
 * {@link BeanLocator} keeps the application context in a static field, so the original value is
 * saved and restored around this class to keep the other tests running in the same JVM unaffected.
 */
class BeanLocatorTest {

    private static ApplicationContext originalApplicationContext;

    @BeforeAll
    static void saveOriginalApplicationContext() throws Exception {
        originalApplicationContext = readApplicationContext();
    }

    @AfterAll
    static void restoreOriginalApplicationContext() throws Exception {
        writeApplicationContext(originalApplicationContext);
    }

    @BeforeEach
    void clearApplicationContext() throws Exception {
        writeApplicationContext(null);
    }

    @Test
    void isNotInitializedBeforeAnyInstanceIsCreated() {
        assertThat(BeanLocator.isInitialized()).isFalse();
    }

    @Test
    void isInitializedOnceAnInstanceIsCreated() {
        new BeanLocator(mock(ApplicationContext.class));

        assertThat(BeanLocator.isInitialized()).isTrue();
    }

    @Test
    void getByIdIsEmptyWhenNotInitialized() {
        assertThat(BeanLocator.<Context>get("testContext")).isEmpty();
    }

    @Test
    void getByTypeIsEmptyWhenNotInitialized() {
        assertThat(BeanLocator.get(Context.class)).isEmpty();
    }

    @Test
    void listIsEmptyWhenNotInitialized() {
        assertThat(BeanLocator.list(Context.class)).isEmpty();
    }

    @Test
    void getByIdDelegatesToTheApplicationContext() {
        final ApplicationContext applicationContext = mock(ApplicationContext.class);
        final Context bean = Context.anonymous();
        when(applicationContext.getBean(eq("testContext"))).thenReturn(bean);
        new BeanLocator(applicationContext);

        final Optional<Context> result = BeanLocator.get("testContext");

        assertThat(result).containsSame(bean);
    }

    @Test
    void getByTypeDelegatesToTheApplicationContext() {
        final ApplicationContext applicationContext = mock(ApplicationContext.class);
        final Context bean = Context.anonymous();
        when(applicationContext.getBean(eq(Context.class))).thenReturn(bean);
        new BeanLocator(applicationContext);

        final Optional<Context> result = BeanLocator.get(Context.class);

        assertThat(result).containsSame(bean);
    }

    @Test
    void listDelegatesToTheApplicationContext() {
        final ApplicationContext applicationContext = mock(ApplicationContext.class);
        final Context bean = Context.anonymous();
        when(applicationContext.getBeansOfType(eq(Context.class)))
                .thenReturn(Collections.singletonMap("testContext", bean));
        new BeanLocator(applicationContext);

        final Map<String, Context> result = BeanLocator.list(Context.class);

        assertThat(result).containsExactly(org.assertj.core.api.Assertions.entry("testContext", bean));
    }

    private static ApplicationContext readApplicationContext() throws Exception {
        return (ApplicationContext) applicationContextField().get(null);
    }

    private static void writeApplicationContext(ApplicationContext applicationContext) throws Exception {
        applicationContextField().set(null, applicationContext);
    }

    private static Field applicationContextField() throws Exception {
        final Field field = BeanLocator.class.getDeclaredField("applicationContext");
        field.setAccessible(true);
        return field;
    }
}
