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
package be.atbash.demo.spring.rest.web;

import be.atbash.demo.spring.rest.dto.CompanyDTO;
import be.atbash.demo.spring.rest.dto.CompanyWithIdDTO;
import be.atbash.demo.spring.rest.exception.DomainErrorCodes;
import be.atbash.demo.spring.rest.jupiter.ExcelDataSet;
import be.atbash.demo.spring.rest.model.Company;
import be.atbash.demo.spring.rest.web.checks.BusinessValidationMvcResultCheck;
import jakarta.inject.Provider;
import org.assertj.core.api.Assertions;
import org.dbunit.database.IDatabaseConnection;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@ExcelDataSet("classpath:/data/EmployeeRepository.xlsx")
class CompanyControllerIT extends AbstractEndpointTest {

    @Autowired
    protected Provider<IDatabaseConnection> databaseConnectionProvider;

    @Test
    void getCompanyByName() throws Exception {

        // act
        CompanyWithIdDTO companyWithIdDTO = performGet("/company/Atbash", CompanyWithIdDTO.class, MockMvcResultMatchers.status().isOk());

        // assert
        Assertions.assertThat(companyWithIdDTO).isNotNull();
        Assertions.assertThat(companyWithIdDTO.id()).isEqualTo(-2L);
        Assertions.assertThat(companyWithIdDTO.name()).isEqualTo("Atbash");
    }

    @Test
    void getCompanyByName_unknown() throws Exception {

        // act
        performGet("/company/something", CompanyWithIdDTO.class, MockMvcResultMatchers.status().isBadRequest());

        // assert
    }

    @Test
    void createCompany() throws Exception {

        // arrange
        int beforeCreate = getRowCountFor("company");

        // act
        CompanyWithIdDTO companyWithIdDTO = performPost("/company", new CompanyDTO("Test"), CompanyWithIdDTO.class, MockMvcResultMatchers.status().isCreated());

        // assert
        Assertions.assertThat(companyWithIdDTO).isNotNull();
        Assertions.assertThat(companyWithIdDTO.name()).isEqualTo("Test");

        // test just we have an additional record
        Assertions.assertThat(getRowCountFor("company")).isEqualTo(beforeCreate + 1);

        // or load the records and check their values, here Company name.
        List<Company> companies = getRowsFor("company", Company.class);

        Set<String> companyNames = companies.stream()
                .map(Company::getName)
                .collect(Collectors.toSet());

        Assertions.assertThat(companyNames).containsExactlyInAnyOrder("JUnit", "Atbash", "Test");

    }

    @Test
    void createCompany_duplicateName() throws Exception {

        // arrange
        int beforeCreate = getRowCountFor("company");

        // act and assert
        performPost("/company", new CompanyDTO("Atbash"), CompanyWithIdDTO.class,
                new BusinessValidationMvcResultCheck(DomainErrorCodes.COMPANY_NAME_ALREADY_EXISTS, "The company name already exists"));

        // test just we have no additional record
        Assertions.assertThat(getRowCountFor("company")).isEqualTo(beforeCreate);


    }

    @Test
    void createCompany_validationIssue() throws Exception {

        // arrange
        int beforeCreate = getRowCountFor("company");

        // act
        performPost("/company", new CompanyDTO(""), CompanyWithIdDTO.class, MockMvcResultMatchers.status().isBadRequest());

        // assert
        // no record added
        Assertions.assertThat(getRowCountFor("company")).isEqualTo(beforeCreate);
    }

    @Test
    void deleteCompany() throws Exception {
        // arrange

        int recordCount = performQuery("SELECT c.* FROM company c WHERE c.name = 'JUnit'", Company.class).size();
        Assertions.assertThat(recordCount).isEqualTo(1);  // Check if record exist

        // act
        performDelete("/company/JUnit", MockMvcResultMatchers.status().isNoContent());

        // assert

        List<Company> companies = getRowsFor("company", Company.class);
        Assertions.assertThat(companies).extracting(Company::getName).containsExactly("Atbash");
    }

    @Test
    void deleteCompany_unknown() throws Exception {

        // arrange

        int recordCount = performQuery("SELECT c.* FROM company c WHERE c.name = 'xxx'", Company.class).size();
        Assertions.assertThat(recordCount).isEqualTo(0);  // Check if record does not exist

        // act
        performDelete("/company/xxx", MockMvcResultMatchers.status().isNoContent());
        // idempotent
    }
}