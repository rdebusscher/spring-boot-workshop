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
package be.atbash.demo.spring.rest.web;

import be.atbash.demo.spring.rest.service.LoanCalculator;
import be.atbash.demo.spring.rest.web.dto.LoanParametersDTO;
import be.atbash.demo.spring.rest.web.dto.LoanResponseDTO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoanController {

    private final LoanCalculator loanCalculator;

    public LoanController(LoanCalculator loanCalculator) {
        this.loanCalculator = loanCalculator;
    }

    @PostMapping("/loan")
    public LoanResponseDTO calculateMonthlyAmount(@RequestBody LoanParametersDTO loanParametersDTO) {

        return new LoanResponseDTO(loanCalculator.calculateMonthlyAmount(loanParametersDTO.amount(), loanParametersDTO.years()));
    }
}
