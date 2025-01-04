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
package be.atbash.demo.spring.rest.service.impl;

import be.atbash.demo.spring.rest.service.AnnualInterestRateService;
import be.atbash.demo.spring.rest.service.LoanCalculator;
import be.atbash.demo.spring.rest.validation.LoanCalculatorValidationService;
import org.springframework.stereotype.Service;

@Service  // Is just like a Component but has the 'semantic' of holding business logic
public class LoanCalculatorImpl implements LoanCalculator {

    private final LoanCalculatorValidationService loanCalculatorValidationService;
    private final AnnualInterestRateService annualInterestRateService;

    public LoanCalculatorImpl(LoanCalculatorValidationService loanCalculatorValidationService
            , AnnualInterestRateService annualInterestRateService) {
        this.loanCalculatorValidationService = loanCalculatorValidationService;
        // Constructor Injection
        this.annualInterestRateService = annualInterestRateService;
    }


    public double calculateMonthlyAmount(double amount, int years) {
        loanCalculatorValidationService.validateParameters(amount, years);
        double annualInterestRate = annualInterestRateService.getCurrentValue();

        double monthlyRate = annualInterestRate / 12.0 / 100.0;
        int months = years * 12;

        double intermediate = Math.pow(1.0 + monthlyRate, months);

        //A  = P {[r (1+r)^n ]/ [(1+r)^n - 1]}
        //
        return amount * monthlyRate * intermediate / (intermediate - 1);

    }

}
