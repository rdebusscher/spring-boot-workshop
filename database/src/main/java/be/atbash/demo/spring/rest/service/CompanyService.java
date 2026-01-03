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
package be.atbash.demo.spring.rest.service;


import be.atbash.demo.spring.rest.repository.EmployeeRepository;
import be.atbash.demo.spring.rest.validation.CompanyValidationService;
import be.atbash.demo.spring.rest.dto.CompanyDTO;
import be.atbash.demo.spring.rest.dto.CompanyDTOWithId;
import be.atbash.demo.spring.rest.exception.BusinessValidationException;
import be.atbash.demo.spring.rest.exception.DomainErrorCodes;
import be.atbash.demo.spring.rest.mapper.CompanyMapperService;
import be.atbash.demo.spring.rest.model.Company;
import be.atbash.demo.spring.rest.repository.CompanyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final EmployeeRepository employeeRepository;
    private final CompanyMapperService companyMapperService;
    private final CompanyValidationService companyValidationService;

    public CompanyService(CompanyRepository companyRepository, EmployeeRepository employeeRepository, CompanyMapperService companyMapperService, CompanyValidationService companyValidationService) {
        this.companyRepository = companyRepository;
        this.employeeRepository = employeeRepository;
        this.companyMapperService = companyMapperService;
        this.companyValidationService = companyValidationService;
    }

    @Transactional(readOnly = true)
    public List<CompanyDTOWithId> getAll() {
        return companyRepository.findAll()
                .stream()
                .map(companyMapperService::asDTOWithId)
                .toList();
    }

    @Transactional(readOnly = true)
    public CompanyDTOWithId getByName(String name) {
        return companyRepository.findByName(name)
                .map(companyMapperService::asDTOWithId)
                .orElseThrow(() -> new BusinessValidationException(DomainErrorCodes.COMPANY_NAME_NOT_FOUND));
    }

    public CompanyDTOWithId create(CompanyDTO companyDTO) {
        companyValidationService.validateCreate(companyDTO);

        Company company = companyMapperService.asEntity(companyDTO);
        company = companyRepository.save(company);
        return companyMapperService.asDTOWithId(company);
    }

    public void deleteByName(String name) {
        // No validation error if company is not found. (idempotent)
        companyRepository.findByName(name).ifPresent(entity -> {
            employeeRepository.deleteAllByCompanyId(entity.getId());
            companyRepository.delete(entity);
        });
    }
}
