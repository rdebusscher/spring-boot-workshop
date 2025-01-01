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
package be.atbach.demo.spring.rest.config;

import be.atbach.demo.spring.rest.exception.BusinessValidationException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.ProblemDetail;

import java.net.URI;
import java.util.Locale;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @Mock
    private ResourceBundleMessageSource resourceBundleMessageSourceMock;

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @Test
    void handleBusinessValidationException() {
        // testing of the exception handling method as JUnit test. Not as a result of a call to an endpoint.
        // See also GlobalExceptionHandlerIT

        // arrange
        BusinessValidationException businessValidationException = new BusinessValidationException("code");

        Mockito.when(resourceBundleMessageSourceMock.getMessage(businessValidationException.getCode(), businessValidationException.getParameters(), Locale.ENGLISH))
                .thenReturn("message");

        // act
        ProblemDetail problemDetail = globalExceptionHandler.handleBusinessValidationException(businessValidationException);

        // assert
        Assertions.assertThat(problemDetail).isNotNull();
        Assertions.assertThat(problemDetail.getTitle()).isEqualTo("Validation problem");
        Assertions.assertThat(problemDetail.getDetail()).isEqualTo("message");
        Assertions.assertThat(problemDetail.getProperties()).isNotNull();
        Assertions.assertThat(problemDetail.getProperties().get("code")).isEqualTo("code");
        Assertions.assertThat(problemDetail.getType()).isEqualTo(URI.create("https://api.atbash.be/errors/business-validation-problem"));

    }
}