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
package com.linecorp.flagship4j.javaflagr.enums;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class EvaluationContextFlagTagsOperatorTest {

    @Test
    void declaresAnyAndAllOperators() {
        assertArrayEquals(
                new EvaluationContextFlagTagsOperator[] {
                        EvaluationContextFlagTagsOperator.ANY,
                        EvaluationContextFlagTagsOperator.ALL },
                EvaluationContextFlagTagsOperator.values());
    }

    @Test
    void valueOfResolvesDeclaredNames() {
        assertSame(EvaluationContextFlagTagsOperator.ANY, EvaluationContextFlagTagsOperator.valueOf("ANY"));
        assertSame(EvaluationContextFlagTagsOperator.ALL, EvaluationContextFlagTagsOperator.valueOf("ALL"));
    }

    @Test
    void valueOfRejectsUnknownName() {
        assertThrows(IllegalArgumentException.class, () -> EvaluationContextFlagTagsOperator.valueOf("NONE"));
    }
}
