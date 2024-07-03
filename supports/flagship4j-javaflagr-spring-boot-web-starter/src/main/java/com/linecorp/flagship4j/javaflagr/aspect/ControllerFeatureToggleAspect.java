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

import com.linecorp.flagship4j.javaflagr.service.FlagrEvalService;
import com.linecorp.flagship4j.javaflagr.annotations.ControllerFeatureToggle;
import com.linecorp.flagship4j.javaflagr.annotations.VariantKey;
import com.linecorp.flagship4j.javaflagr.exception.FlagrApiNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

@Aspect
@RequiredArgsConstructor
@Slf4j
public class ControllerFeatureToggleAspect {

    private final FlagrEvalService flagrService;

    @Around("@annotation(com.linecorp.flagship4j.javaflagr.annotations.ControllerFeatureToggle)")
    public Object processControllerFeatureToggleAnnotation(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("start processing controllerFeatureToggle annotation");
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        Object[] args = joinPoint.getArgs();
        ControllerFeatureToggle featureToggle = method.getAnnotation(ControllerFeatureToggle.class);

        Boolean isFlagOn = flagrService.isFeatureFlagOn(featureToggle.value());

        if (Boolean.FALSE.equals(isFlagOn)) {
            throw new FlagrApiNotFoundException();
        }

        outerlabel:
        for (int argIndex = 0; argIndex < args.length; argIndex++) {
            for (Annotation annotation : parameterAnnotations[argIndex]) {
                if (annotation instanceof VariantKey) {
                    args[argIndex] = isFlagOn;
                    break outerlabel;
                }
            }
        }

        return joinPoint.proceed(args);
    }
}
