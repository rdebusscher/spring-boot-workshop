/*
 * Copyright 2024-2026 Rudy De Busscher (https://www.atbash.be)
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

import be.atbash.demo.spring.rest.dto.EmployeeWithoutIdDTO;
import be.atbash.demo.spring.rest.exception.BusinessValidationException;
import be.atbash.demo.spring.rest.exception.DomainErrorCodes;
import be.atbash.demo.spring.rest.model.Company;
import be.atbash.demo.spring.rest.repository.EmployeeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true, propagation = Propagation.MANDATORY)
// Can only be used from a service that started the transaction
public class EmployeeValidationService {

    private final EmployeeRepository employeeRepository;
    private final CompanyValidationService companyValidationService;

    public EmployeeValidationService(EmployeeRepository employeeRepository, CompanyValidationService companyValidationService) {
        this.employeeRepository = employeeRepository;
        this.companyValidationService = companyValidationService;
    }

    public Company validateCreate(EmployeeWithoutIdDTO dto) {
        Company company = companyValidationService.checkValidName(dto.company().name());
        shouldFailWhenEmailAlreadyInUse(dto.email(), company.getId());
        return company;
    }

    protected void shouldFailWhenEmailAlreadyInUse(String email, Long companyId) {
        employeeRepository.findByEmail(email, companyId)
                .ifPresent(e -> {
                    throw new BusinessValidationException(DomainErrorCodes.EMPLOYEE_EMAIL_ALREADY_IN_USE);
                });
    }
}
