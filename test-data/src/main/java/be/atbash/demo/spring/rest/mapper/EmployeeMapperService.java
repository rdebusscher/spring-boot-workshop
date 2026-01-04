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
import be.atbash.demo.spring.rest.dto.EmployeeWithoutIdDTO;
import be.atbash.demo.spring.rest.model.Employee;
import org.springframework.stereotype.Component;

@Component
public class EmployeeMapperService {

    private final CompanyMapperService companyMapperService;

    public EmployeeMapperService(CompanyMapperService companyMapperService) {
        this.companyMapperService = companyMapperService;
    }


    public EmployeeWithIdDTO asDtoWithId(Employee employee) {
        return new EmployeeWithIdDTO(
                employee.getId(),
                employee.getEmail(),
                employee.getFirstName(),
                employee.getLastName(),
                employee.getHireDate(),
                employee.getGender(),
                companyMapperService.asDTO(employee.getCompany())
        );
    }

    public Employee asEntity(EmployeeWithoutIdDTO employeeWithIdDto) {
        Employee employee = new Employee();

        employee.setEmail(employeeWithIdDto.email());
        employee.setFirstName(employeeWithIdDto.firstName());
        employee.setLastName(employeeWithIdDto.lastName());
        employee.setHireDate(employeeWithIdDto.hireDate());
        employee.setGender(employeeWithIdDto.gender());
        // no need for trying to set or map Company
        return employee;
    }
}
