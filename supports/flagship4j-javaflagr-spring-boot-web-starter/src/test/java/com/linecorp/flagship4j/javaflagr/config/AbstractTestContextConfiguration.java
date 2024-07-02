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

import com.linecorp.flagship4j.javaflagr.bean.BeanLocator;
import com.linecorp.flagship4j.javaflagr.model.Context;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

public abstract class AbstractTestContextConfiguration implements TestContextConfiguration {

    @Bean
    @Primary
    public BeanLocator beanLocator(ApplicationContext applicationContext) {
        return new BeanLocator(applicationContext);
    }

    @Bean
    @Primary
    public abstract Context testContext();

}
