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
package com.linecorp.flagship4j.javaflagr.aspect;

import com.linecorp.flagship4j.javaflagr.exception.FlagrApiNotFoundException;
import com.linecorp.flagship4j.javaflagr.model.enums.EffectiveVariant;
import com.linecorp.flagship4j.javaflagr.service.FlagrEvalService;
import com.linecorp.flagship4j.javaflagr.TestFlagrDataGenerator;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.lang.reflect.Method;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class ControllerFeatureToggleAspectTest extends TestFlagrDataGenerator {
    @InjectMocks
    private ControllerFeatureToggleAspect featureToggleAspect;

    @Mock
    private FlagrEvalService flagrService;

    @Mock
    private ProceedingJoinPoint joinPoint;

    @Mock
    private MethodSignature signature;

    private Object[] args = {new Object()};

    @Test
    public void processFlagrMethodWithControllerFeatureToggleTest() throws Throwable {
        String methodName = "methodWithControllerFeatureToggle";
        FlagrAnnotationTest flagrAnnotationTest = new FlagrAnnotationTest();
        Method method = Arrays.stream(flagrAnnotationTest.getClass().getMethods()).filter(m -> m.getName().equals(methodName)).findFirst().get();

        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.getMethod()).thenReturn(method);
        when(joinPoint.getArgs()).thenReturn(args);
        when(flagrService.isFeatureFlagOn(any(String.class)))
                .thenReturn(givenPostEvaluationResponse().getVariantKey().equals(EffectiveVariant.ON.toValue()));
        when(joinPoint.proceed(any(Object[].class))).thenReturn(args);

        Object returnArgs = featureToggleAspect.processControllerFeatureToggleAnnotation(joinPoint);

        assertEquals(args, returnArgs);
        verify(joinPoint, times(1)).getSignature();
        verify(signature, times(1)).getMethod();
        verify(joinPoint, times(1)).getArgs();
        verify(signature, times(1)).getMethod();
        verify(flagrService, times(1)).isFeatureFlagOn(any(String.class));
        verify(joinPoint, times(1)).proceed(any(Object[].class));
    }

    @Test
    public void processFlagrMethodWithControllerFeatureToggleTestWhenReturnedFlagKeyIsFalseFlagrApiNotFoundException() {
        String methodName = "methodWithControllerFeatureToggleWithoutVariantKey";
        FlagrAnnotationTest flagrAnnotationTest = new FlagrAnnotationTest();
        Method method = Arrays.stream(flagrAnnotationTest.getClass().getMethods()).filter(m -> m.getName().equals(methodName)).findFirst().get();

        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.getMethod()).thenReturn(method);
        when(joinPoint.getArgs()).thenReturn(args);
        when(flagrService.isFeatureFlagOn(any(String.class)))
                .thenReturn(givenPostEvaluationResponse().getVariantKey().equals(EffectiveVariant.OFF.toValue()));

        assertThrows(FlagrApiNotFoundException.class, () -> featureToggleAspect.processControllerFeatureToggleAnnotation(joinPoint));

        verify(joinPoint, times(1)).getSignature();
        verify(signature, times(1)).getMethod();
        verify(joinPoint, times(1)).getArgs();
        verify(signature, times(1)).getMethod();
        verify(flagrService, times(1)).isFeatureFlagOn(any(String.class));
    }

    @Test
    public void processFlagrMethodWithControllerFeatureToggleTestWhenNotVariantKeyAnnotation() throws Throwable {
        String methodName = "methodWithControllerFeatureToggleWithoutVariantKey";
        FlagrAnnotationTest flagrAnnotationTest = new FlagrAnnotationTest();
        Method method = Arrays.stream(flagrAnnotationTest.getClass().getMethods()).filter(m -> m.getName().equals(methodName)).findFirst().get();
        args = new Object[]{};

        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.getMethod()).thenReturn(method);
        when(joinPoint.getArgs()).thenReturn(args);
        when(flagrService.isFeatureFlagOn(any(String.class)))
                .thenReturn(givenPostEvaluationResponse().getVariantKey().equals(EffectiveVariant.ON.toValue()));
        when(joinPoint.proceed(any(Object[].class))).thenReturn(args);

        Object returnArgs = featureToggleAspect.processControllerFeatureToggleAnnotation(joinPoint);

        assertEquals(args, returnArgs);
        verify(joinPoint, times(1)).getSignature();
        verify(signature, times(1)).getMethod();
        verify(joinPoint, times(1)).getArgs();
        verify(signature, times(1)).getMethod();
        verify(flagrService, times(1)).isFeatureFlagOn(any(String.class));
        verify(joinPoint, times(1)).proceed(any(Object[].class));
    }
}
