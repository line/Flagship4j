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
package com.linecorp.flagship4j.javaflagr.bean;

import org.springframework.context.ApplicationContext;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class BeanLocator {

    private static ApplicationContext applicationContext;

    public static Boolean isInitialized() {
        return Objects.nonNull(applicationContext);
    }

    public static <T> Optional<T> get(String id) {
        if (!isInitialized()) {
            return Optional.empty();
        }

        return Optional.ofNullable((T) applicationContext.getBean(id));
    }

    public static <T> Optional<T> get(Class<? extends T> type) {
        if (!isInitialized()) {
            return Optional.empty();
        }

        return Optional.ofNullable(applicationContext.getBean(type));
    }

    public static <T> Map<String, T> list(Class<? extends T> type) {
        if (!isInitialized()) {
            return Collections.emptyMap();
        }

        return (Map<String, T>) applicationContext.getBeansOfType(type);
    }

    public BeanLocator(ApplicationContext applicationContext) {
        BeanLocator.applicationContext = applicationContext;
    }

}
