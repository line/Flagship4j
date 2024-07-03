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

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import com.linecorp.flagship4j.javaflagr.exception.ErrorResponseException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SecureRandomUtils {
    /*
        /dev/random Extremely Slow
        NativePRNG (nextBytes() uses /dev/urandom, generateSeed() uses /dev/random)
        Ref: https://stackoverflow.com/questions/26021181/not-enough-entropy-to-support-dev-random-in-docker-containers-running-in-boot2d
    */
    private static final String DEFAULT_SECURE_RANDOM_ALGORITHM = "NativePRNG";

    public static Integer generateInteger(Integer bound) {
        return getSecureRandom(DEFAULT_SECURE_RANDOM_ALGORITHM).nextInt(bound);
    }

    public static Long generateLong() {
        return getSecureRandom(DEFAULT_SECURE_RANDOM_ALGORITHM).nextLong();
    }

    private static SecureRandom getSecureRandom(String algorithm) {
        try {
            return SecureRandom.getInstance(algorithm);
        } catch (NoSuchAlgorithmException e) {
            throw new ErrorResponseException(e);
        }
    }
}
