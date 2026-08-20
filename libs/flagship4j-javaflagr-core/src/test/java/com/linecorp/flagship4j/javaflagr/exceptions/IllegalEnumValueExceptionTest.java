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
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class IllegalEnumValueExceptionTest {

    @Test
    void causedByBlankValueCarriesNoEnumValue() {
        final IllegalEnumValueException ex = IllegalEnumValueException.causedByBlankValue();

        assertEquals(IllegalEnumValueException.CAUSED_BY_BLANK_VALUE_MESSAGE, ex.getMessage());
        assertNull(ex.getEnumValue());
    }

    @Test
    void causedByInvalidValueKeepsTheRejectedValue() {
        final IllegalEnumValueException ex = IllegalEnumValueException.causedByInvalidValue("maybe");

        assertEquals(
                String.format(IllegalEnumValueException.CAUSED_BY_INVALID_VALUE_MESSAGE_FORMAT, "maybe"),
                ex.getMessage());
        assertEquals("maybe", ex.getEnumValue());
    }

    @Test
    void causedByInvalidValueRendersNonStringValues() {
        final IllegalEnumValueException ex = IllegalEnumValueException.causedByInvalidValue(Integer.valueOf(42));

        assertEquals("Cannot create enum from 42.", ex.getMessage());
        assertEquals(Integer.valueOf(42), ex.getEnumValue());
    }

    @Test
    void isAnIllegalArgumentException() {
        assertTrue(IllegalArgumentException.class.isAssignableFrom(IllegalEnumValueException.class));
    }
}
