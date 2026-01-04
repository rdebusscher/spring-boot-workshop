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

import be.atbash.demo.spring.rest.dto.CompanyDTO;
import be.atbash.demo.spring.rest.dto.CompanyWithIdDTO;
import be.atbash.demo.spring.rest.model.Company;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class CompanyMapperServiceTest {

    private final CompanyMapperService companyMapperService = new CompanyMapperService();
    // If we have dependencies, we can use Mockito, @Mock and @InjectMock.

    @Test
    void asDTOWithId() {
        // arrange
        Company company = new Company();
        company.setId(1L);
        company.setName("Atbash");

        // act
        CompanyWithIdDTO companyWithIdDTO = companyMapperService.asDTOWithId(company);

        // assert
        Assertions.assertThat(companyWithIdDTO).isNotNull();
        Assertions.assertThat(companyWithIdDTO.id()).isEqualTo(1L);
        Assertions.assertThat(companyWithIdDTO.name()).isEqualTo("Atbash");

    }

    @Test
    void asDTO() {
        // arrange
        Company company = new Company();
        company.setName("Atbash");

        // act
        CompanyDTO companyDTO = companyMapperService.asDTO(company);

        // assert
        Assertions.assertThat(companyDTO).isNotNull();
        Assertions.assertThat(companyDTO.name()).isEqualTo("Atbash");
    }

    @Test
    void asEntity() {
        // arrange
        CompanyDTO companyDTO = new CompanyDTO("Atbash");

        // act
        Company company = companyMapperService.asEntity(companyDTO);

        // assert
        Assertions.assertThat(company).isNotNull();
        Assertions.assertThat(company.getName()).isEqualTo("Atbash");
        Assertions.assertThat(company.getId()).isNull();
    }
}