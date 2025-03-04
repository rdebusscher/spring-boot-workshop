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
package be.atbash.demo.spring.helper;

import be.atbash.demo.spring.rest.exception.BusinessValidationException;
import org.assertj.core.api.Assertions;

public final class BusinessValidationExceptionAssertions {

    private BusinessValidationExceptionAssertions() {
    }

    public static void assertException(Throwable exception, String errorMessage, Object[] expectedParameters) {
        Assertions.assertThat(exception)
                .isInstanceOf(BusinessValidationException.class);
        Assertions.assertThat(exception.getMessage()).isEqualTo(errorMessage);

        BusinessValidationException businessValidationException = (BusinessValidationException) exception;
        Assertions.assertThat(businessValidationException.getParameters())
                .as("Checking parameters of BusinessValidationException")
                .containsExactly(expectedParameters);
    }
}
