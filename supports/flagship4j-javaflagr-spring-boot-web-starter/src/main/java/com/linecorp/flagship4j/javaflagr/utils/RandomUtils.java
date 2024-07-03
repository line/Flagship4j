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
package com.linecorp.flagship4j.javaflagr.utils;

import java.nio.charset.Charset;

import org.apache.commons.lang3.RandomStringUtils;
import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RandomUtils {
    private static final EasyRandomParameters DEFAULT_EASY_RANDOM_PARAMETERS_TEMPLATE =
            new EasyRandomParameters()
                    .objectPoolSize(100)
                    .randomizationDepth(3)
                    .charset(Charset.forName("UTF-8"))
                    .collectionSizeRange(1, 10)
                    .scanClasspathForConcreteTypes(true)
                    .overrideDefaultInitialization(false)
                    .ignoreRandomizationErrors(true);

    public static <T> T generateObject(Class<T> type) {
        return generateObject(type, DEFAULT_EASY_RANDOM_PARAMETERS_TEMPLATE);
    }

    public static <T> T generateObject(Class<T> type, EasyRandomParameters parametersTemplate) {
        EasyRandomParameters parameters = withRandomSeed(parametersTemplate);
        return new EasyRandom(parameters).nextObject(type);
    }

    public static String generateAlphanumericString(Integer count) {
        return RandomStringUtils.randomAlphanumeric(count);
    }

    public static String generateNumericString(Integer count) {
        return RandomStringUtils.randomNumeric(count);
    }

    private static EasyRandomParameters withRandomSeed(EasyRandomParameters parameters) {
        return parameters.seed(SecureRandomUtils.generateLong());
    }
}
