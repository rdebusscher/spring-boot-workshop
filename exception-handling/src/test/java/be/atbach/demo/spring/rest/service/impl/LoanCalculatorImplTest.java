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
package be.atbach.demo.spring.rest.service.impl;

import be.atbach.demo.spring.rest.service.AnnualInterestRateService;
import be.atbach.demo.spring.rest.validation.LoanCalculatorValidationService;
import org.assertj.core.api.Assertions;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LoanCalculatorImplTest {

    @Mock
    private LoanCalculatorValidationService loanCalculatorValidationServiceMock;
    @Mock
    private AnnualInterestRateService annualInterestRateServiceMock;

    @InjectMocks
    private LoanCalculatorImpl loanCalculator;

    @Test
    void calculateMonthlyAmount() {
        // arrange
        Mockito.when(annualInterestRateServiceMock.getCurrentValue()).thenReturn(6.99);

        double totalAmount = 100_000.0;
        int years = 15;

        // act
        double amount = loanCalculator.calculateMonthlyAmount(totalAmount, years);

        // assert
        Assertions.assertThat(amount).isEqualTo(898.269, Offset.offset(0.001));  // heck calculation
        Mockito.verify(loanCalculatorValidationServiceMock).validateParameters(totalAmount, years);  // check if validation is performed.
    }
}