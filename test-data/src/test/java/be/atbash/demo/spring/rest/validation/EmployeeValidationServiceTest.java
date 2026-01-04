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
import be.atbash.demo.spring.rest.dto.EmployeeWithoutIdDTO;
import be.atbash.demo.spring.rest.exception.BusinessValidationException;
import be.atbash.demo.spring.rest.exception.DomainErrorCodes;
import be.atbash.demo.spring.rest.model.Company;
import be.atbash.demo.spring.rest.model.Employee;
import be.atbash.demo.spring.rest.repository.EmployeeRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class EmployeeValidationServiceTest {

    private static final String EMAIL = "email";
    private static final Long COMPANY_ID = 1L;


    @Mock
    private EmployeeRepository employeeRepository;
    @Mock
    private CompanyValidationService companyValidationService;


    @InjectMocks
    private EmployeeValidationService employeeValidationService;

    @Test
    void validateCreate() {
        // arrange
        EmployeeValidationService spy = Mockito.spy(employeeValidationService);

        String companyName = "company";

        Company companyDB = new Company();
        companyDB.setId(COMPANY_ID);
        Mockito.when(companyValidationService.checkValidName(companyName))
                .thenReturn(companyDB);

        EmployeeWithoutIdDTO dto = new EmployeeWithoutIdDTO(EMAIL, null, null, null, null, new CompanyDTO(companyName));
        Mockito.doNothing().when(spy).shouldFailWhenEmailAlreadyInUse(EMAIL, COMPANY_ID);

        // act
        Company company = spy.validateCreate(dto);

        // assert
        Assertions.assertThat(company).isSameAs(companyDB);
        Mockito.verify(spy).shouldFailWhenEmailAlreadyInUse(EMAIL, COMPANY_ID);
        Mockito.verify(spy).validateCreate(dto);
        Mockito.verifyNoMoreInteractions(spy);
    }

    @Test
    void shouldFailWhenEmailAlreadyInUse() {
        // arrange
        Mockito.when(employeeRepository.findByEmail(EMAIL, COMPANY_ID))
                .thenReturn(Optional.of(new Employee()));

        // act
        Assertions.assertThatThrownBy(() -> employeeValidationService.shouldFailWhenEmailAlreadyInUse(EMAIL, COMPANY_ID))
                .isInstanceOf(BusinessValidationException.class)
                .hasMessage(DomainErrorCodes.EMPLOYEE_EMAIL_ALREADY_IN_USE);


        // assert
    }

    @Test
    void shouldSucceedWhenEmailNotAlreadyInUse() {
        // arrange
        Mockito.when(employeeRepository.findByEmail(EMAIL, COMPANY_ID))
                .thenReturn(Optional.empty());

        // act
        employeeValidationService.shouldFailWhenEmailAlreadyInUse(EMAIL, COMPANY_ID);


        // assert
    }
}