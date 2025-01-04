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
package be.atbash.demo.spring.rest.validation;

import be.atbash.demo.spring.helper.BusinessValidationExceptionAssertions;
import be.atbash.demo.spring.rest.ApplicationConstants;
import be.atbash.demo.spring.rest.exception.BusinessValidationException;
import be.atbash.demo.spring.rest.exception.DomainErrorCodes;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
class LoanCalculatorValidationServiceTest {

    // If validation depends on other services or repositories, add them here with @Mock.

    // The class under test
    @InjectMocks
    private LoanCalculatorValidationService loanCalculatorValidationServiceMock;

    @Test
    void validateParameters() {
        //  arrange
        LoanCalculatorValidationService spy = Mockito.spy(loanCalculatorValidationServiceMock);

        double amount = -123_456.0;  // Should throw an exception, but we want to know if the 'different' parts of the validation are called
        int years = -10;

        Mockito.doNothing().when(spy).validateAmount(amount);  // This ignores the method logic.
        Mockito.doNothing().when(spy).validateYears(years);

        // act
        spy.validateParameters(amount, years);

        // assert
        Mockito.verify(spy).validateAmount(amount);
        Mockito.verify(spy).validateYears(years);

    }

    @Test
    void validateAmount_message() {
        //  arrange

        // act assert
        Assertions.assertThatThrownBy(() -> loanCalculatorValidationServiceMock.validateAmount(-123_456.0))
                .isInstanceOf(BusinessValidationException.class)
                .satisfies(e -> BusinessValidationExceptionAssertions.assertException(e, DomainErrorCodes.PARAMETER_OUT_RANGE, new Object[]{"Amount", 0.0, ApplicationConstants.MAX_AMOUNT_ALLOWED}));
    }

    @ParameterizedTest
    @MethodSource("provideParametersAmount")
    void validateAmount_value(double amount, boolean valid) {

        if (valid) {
            loanCalculatorValidationServiceMock.validateAmount(amount);  // When an exception is throw, JUnit will throw the Assertion error.
        } else {
            Assertions.assertThatThrownBy(() -> loanCalculatorValidationServiceMock.validateAmount(amount))
                    .isInstanceOf(BusinessValidationException.class);

        }
    }

    @Test
    void validateYears_message() {
        //  arrange

        // act assert
        Assertions.assertThatThrownBy(() -> loanCalculatorValidationServiceMock.validateYears(-3))
                .isInstanceOf(BusinessValidationException.class)
                .satisfies(e -> BusinessValidationExceptionAssertions.assertException(e, DomainErrorCodes.PARAMETER_OUT_RANGE, new Object[]{"Years", 0, ApplicationConstants.MAX_YEARS_ALLOWED}));
    }

    @ParameterizedTest
    @MethodSource("provideParametersYears")
    void validateYears_value(int years, boolean valid) {

        if (valid) {
            loanCalculatorValidationServiceMock.validateYears(years);  // When an exception is throw, JUnit will throw the Assertion error.
        } else {
            Assertions.assertThatThrownBy(() -> loanCalculatorValidationServiceMock.validateYears(years))
                    .isInstanceOf(BusinessValidationException.class);

        }
    }
    private static Stream<Arguments> provideParametersAmount() {
        return Stream.of(
                Arguments.of(-1, false),
                Arguments.of(0, false),
                Arguments.of(1, true),
                Arguments.of(ApplicationConstants.MAX_AMOUNT_ALLOWED - 1, true),
                Arguments.of(ApplicationConstants.MAX_AMOUNT_ALLOWED, true),
                Arguments.of(ApplicationConstants.MAX_AMOUNT_ALLOWED + 1, false)
        );
    }

    private static Stream<Arguments> provideParametersYears() {
        return Stream.of(
                Arguments.of(-1, false),
                Arguments.of(0, false),
                Arguments.of(1, true),
                Arguments.of(ApplicationConstants.MAX_YEARS_ALLOWED - 1, true),
                Arguments.of(ApplicationConstants.MAX_YEARS_ALLOWED, true),
                Arguments.of(ApplicationConstants.MAX_YEARS_ALLOWED + 1, false)
        );

    }
}