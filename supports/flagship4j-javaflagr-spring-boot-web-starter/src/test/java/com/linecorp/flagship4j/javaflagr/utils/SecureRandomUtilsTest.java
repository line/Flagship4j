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
package com.linecorp.flagship4j.javaflagr.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mockStatic;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import com.linecorp.flagship4j.javaflagr.exception.ErrorResponseException;

class SecureRandomUtilsTest {

    @Test
    void generateIntegerStaysWithinTheGivenBound() {
        for (int i = 0; i < 100; i++) {
            assertThat(SecureRandomUtils.generateInteger(10)).isBetween(0, 9);
        }
    }

    @Test
    void generateIntegerWithABoundOfOneAlwaysReturnsZero() {
        assertThat(SecureRandomUtils.generateInteger(1)).isZero();
    }

    @Test
    void generateLongReturnsVaryingValues() {
        final Set<Long> generated = new HashSet<>();
        for (int i = 0; i < 20; i++) {
            generated.add(SecureRandomUtils.generateLong());
        }

        assertThat(generated).hasSizeGreaterThan(1);
    }

    @Test
    void wrapsAnUnsupportedAlgorithmIntoAnErrorResponseException() {
        try (MockedStatic<SecureRandom> secureRandom = mockStatic(SecureRandom.class)) {
            secureRandom.when(() -> SecureRandom.getInstance("NativePRNG"))
                        .thenThrow(new NoSuchAlgorithmException("NativePRNG not available"));

            assertThatThrownBy(() -> SecureRandomUtils.generateLong())
                    .isInstanceOf(ErrorResponseException.class)
                    .hasCauseInstanceOf(NoSuchAlgorithmException.class);
        }
    }
}
