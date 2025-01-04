/*
 * Copyright 2024-2025 Rudy De Busscher (https://www.atbash.be)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package be.atbash.demo.spring.rest.config;

import be.atbash.demo.spring.rest.exception.BusinessValidationException;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.URI;
import java.util.Locale;

/**
 * Activates the Problem Detail JSON response and adds support for business validation exceptions.
 */
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    public GlobalExceptionHandler(ResourceBundleMessageSource messageSource) {
        setMessageSource(messageSource);
    }

    @ExceptionHandler(BusinessValidationException.class)
    ProblemDetail handleBusinessValidationException(BusinessValidationException e) {

        String message = getMessage(e.getCode(), e.getParameters());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, message);
        problemDetail.setTitle("Validation problem");
        problemDetail.setType(URI.create("https://api.atbash.be/errors/business-validation-problem"));  // URI does not exists pon internet but might
        problemDetail.setProperty("code", e.getCode());
        return problemDetail;
    }

    private String getMessage(String code, Object[] args) {
        try {
            return getMessageSource().getMessage(code, args, Locale.ENGLISH);
        } catch (NoSuchMessageException e) {
            return "No message found for code: " + code;
        }
    }

}
