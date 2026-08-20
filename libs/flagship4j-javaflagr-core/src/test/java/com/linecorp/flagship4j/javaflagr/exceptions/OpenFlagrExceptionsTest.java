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
package com.linecorp.flagship4j.javaflagr.exceptions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class OpenFlagrExceptionsTest {

    private static final Throwable CAUSE = new IllegalStateException("root cause");

    @Test
    void openFlagrExceptionExposesMessageAndCause() {
        assertNull(new OpenFlagrException("boom").getCause());
        assertEquals("boom", new OpenFlagrException("boom").getMessage());
        assertSame(CAUSE, new OpenFlagrException("boom", CAUSE).getCause());
    }

    @Test
    void openFlagrNoFlagKeyExceptionExposesMessageAndCause() {
        assertNull(new OpenFlagrNoFlagKeyException("no flag").getCause());
        assertEquals("no flag", new OpenFlagrNoFlagKeyException("no flag").getMessage());
        assertSame(CAUSE, new OpenFlagrNoFlagKeyException("no flag", CAUSE).getCause());
    }

    @Test
    void openFlagrNoVariantExceptionExposesMessageAndCause() {
        assertNull(new OpenFlagrNoVariantException("no variant").getCause());
        assertEquals("no variant", new OpenFlagrNoVariantException("no variant").getMessage());
        assertSame(CAUSE, new OpenFlagrNoVariantException("no variant", CAUSE).getCause());
    }

    @Test
    void allOpenFlagrExceptionsAreUnchecked() {
        assertTrue(RuntimeException.class.isAssignableFrom(OpenFlagrException.class));
        assertTrue(RuntimeException.class.isAssignableFrom(OpenFlagrNoFlagKeyException.class));
        assertTrue(RuntimeException.class.isAssignableFrom(OpenFlagrNoVariantException.class));
    }
}
