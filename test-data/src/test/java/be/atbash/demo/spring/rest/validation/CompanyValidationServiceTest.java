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

import be.atbash.demo.spring.rest.dto.CompanyDTO;
import be.atbash.demo.spring.rest.exception.BusinessValidationException;
import be.atbash.demo.spring.rest.exception.DomainErrorCodes;
import be.atbash.demo.spring.rest.model.Company;
import be.atbash.demo.spring.rest.repository.CompanyRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class CompanyValidationServiceTest {

    @Mock
    private CompanyRepository companyRepositoryMock;

    @InjectMocks
    private CompanyValidationService companyValidationService;

    @Test
    void validateCreate() {
        // arrange
        CompanyDTO company = new CompanyDTO("Atbash");
        Mockito.when(companyRepositoryMock.findByName(company.name())).thenReturn(Optional.empty());

        // act
        companyValidationService.validateCreate(company);

        // assert
    }

    @Test
    void validateCreate_existingName() {
        // arrange
        CompanyDTO company = new CompanyDTO("Atbash");

        Mockito.when(companyRepositoryMock.findByName(company.name())).thenReturn(Optional.of(new Company()));
        // no need to return the actual company instance, we just check on the Optional.isPresent

        // act
        Assertions.assertThatThrownBy(() -> companyValidationService.validateCreate(company))
                .isInstanceOf(BusinessValidationException.class)
                .hasMessage(DomainErrorCodes.COMPANY_NAME_ALREADY_EXISTS);

        // assert
    }

    @Test
    void checkValidName() {
        // arrange
        String name = "Atbash";
        Company companyDB = new Company();
        Mockito.when(companyRepositoryMock.findByName(name)).thenReturn(Optional.of(companyDB));

        // act
        Company company = companyValidationService.checkValidName(name);

        // assert
        Assertions.assertThat(company).isSameAs(companyDB);
    }

    @Test
    void checkValidName_unknown() {
        // arrange
        String name = "Atbash";
        Mockito.when(companyRepositoryMock.findByName(name)).thenReturn(Optional.empty());

        // act
        Assertions.assertThatThrownBy(() -> companyValidationService.checkValidName(name))
                .isInstanceOf(BusinessValidationException.class)
                .hasMessage(DomainErrorCodes.COMPANY_NAME_NOT_FOUND);

    }
}