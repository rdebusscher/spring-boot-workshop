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
package be.atbash.demo.spring.rest.mapper;

import be.atbash.demo.spring.rest.dto.EmployeeWithIdDTO;
import be.atbash.demo.spring.rest.helper.MapperUtil;
import be.atbash.demo.spring.rest.model.Company;
import be.atbash.demo.spring.rest.model.Employee;
import be.atbash.demo.spring.rest.model.Gender;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

class EmployeeMapperServiceTest {

    // This always gets a fully functional mapper including the dependencies. As long as the mapper Service only
    // depends on other mappers (as it should be the case)
    private final EmployeeMapperService employeeMapperService = MapperUtil.getMapper(EmployeeMapperService.class);

    @Test
    void asDtoWithId() {
        // arrange
        Employee employee = new Employee();
        employee.setId(11L);
        employee.setEmail("john.doe@acme.org");
        employee.setFirstName("John");
        employee.setLastName("Doe");
        employee.setHireDate(LocalDate.of(2020, 1, 1));
        employee.setGender(Gender.MALE);

        Company atbash = new Company();
        atbash.setId(1L);
        atbash.setName("Atbash");
        employee.setCompany(atbash);

        // act
        EmployeeWithIdDTO employeeWithIdDto = employeeMapperService.asDtoWithId(employee);

        // assert
        Assertions.assertThat(employeeWithIdDto).isNotNull();
        Assertions.assertThat(employeeWithIdDto.id()).isEqualTo(11L);
        Assertions.assertThat(employeeWithIdDto.email()).isEqualTo("john.doe@acme.org");
        Assertions.assertThat(employeeWithIdDto.firstName()).isEqualTo("John");
        Assertions.assertThat(employeeWithIdDto.lastName()).isEqualTo("Doe");
        Assertions.assertThat(employeeWithIdDto.hireDate()).isEqualTo(LocalDate.of(2020, 1, 1));
        Assertions.assertThat(employeeWithIdDto.gender()).isEqualTo(Gender.MALE);
        Assertions.assertThat(employeeWithIdDto.company()).isNotNull();
        Assertions.assertThat(employeeWithIdDto.company().name()).isEqualTo("Atbash");

    }
}