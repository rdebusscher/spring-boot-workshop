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

import be.atbash.demo.spring.rest.ApplicationConstants;
import be.atbash.demo.spring.rest.exception.BusinessValidationException;
import be.atbash.demo.spring.rest.exception.DomainErrorCodes;
import org.springframework.stereotype.Service;

@Service
public class LoanCalculatorValidationService {

    public void validateParameters(double amount, int years) {
        // split out the different validation so that we can test which validation are performed. See: LoanCalculatorValidationServiceTest
        validateAmount(amount);
        validateYears(years);
    }

    protected void validateAmount(double amount) {
        if (amount <= 0 || amount > ApplicationConstants.MAX_AMOUNT_ALLOWED) {
            throw new BusinessValidationException(DomainErrorCodes.PARAMETER_OUT_RANGE, new Object[]{"Amount", 0.0, ApplicationConstants.MAX_AMOUNT_ALLOWED});
        }
    }

    protected void validateYears(int years) {
        if (years <= 0 || years > ApplicationConstants.MAX_YEARS_ALLOWED) {
            throw new BusinessValidationException(DomainErrorCodes.PARAMETER_OUT_RANGE, new Object[]{"Years", 0, ApplicationConstants.MAX_YEARS_ALLOWED});
        }
    }
}
