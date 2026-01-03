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

import be.atbash.demo.spring.rest.dto.CompanyDTO;
import be.atbash.demo.spring.rest.dto.CompanyDTOWithId;
import be.atbash.demo.spring.rest.exception.BusinessValidationException;
import be.atbash.demo.spring.rest.exception.DomainErrorCodes;
import be.atbash.demo.spring.rest.mapper.CompanyMapperService;
import be.atbash.demo.spring.rest.model.Company;
import be.atbash.demo.spring.rest.repository.CompanyRepository;
import be.atbash.demo.spring.rest.validation.CompanyValidationService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

@ExtendWith(MockitoExtension.class)
class CompanyServiceTest {

    @Mock
    private CompanyRepository companyRepositoryMock;
    @Mock
    private CompanyValidationService companyValidationServiceMock;

    @Spy
    private CompanyMapperService companyMapperServiceSpy;

    @InjectMocks
    private CompanyService companyService;

    @Test
    void getAll() {
        // arrange
        Company company1 = new Company();
        company1.setId(1L);
        company1.setName("Atbash");

        Company company2 = new Company();
        company2.setId(2L);
        company2.setName("JUnit");

        Mockito.when(companyRepositoryMock.findAll()).thenReturn(List.of(company1, company2));

        // act
        List<CompanyDTOWithId> result = companyService.getAll();

        // assert
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result).hasSize(2);
        Assertions.assertThat(result).extracting(CompanyDTOWithId::id).containsExactly(1L, 2L);
        // we don't need to test all returned values. We have a test for CompanyMapperService.
    }

    @Test
    void getByName() {
        // arrange
        Company company = new Company();
        company.setId(1L);
        company.setName("Atbash");

        Mockito.when(companyRepositoryMock.findByName(company.getName())).thenReturn(Optional.of(company));

        // act
        CompanyDTOWithId result = companyService.getByName("Atbash");

        // assert
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.id()).isEqualTo(1L);
        Assertions.assertThat(result.name()).isEqualTo("Atbash");

        Mockito.verify(companyMapperServiceSpy).asDTOWithId(company);
        Mockito.verifyNoInteractions(companyValidationServiceMock);
    }

    @Test
    void getByName_unknown() {
        // arrange
        String name = "Atbash";

        Mockito.when(companyRepositoryMock.findByName(name)).thenReturn(Optional.empty());

        // act
        Assertions.assertThatThrownBy(() -> companyService.getByName(name))
                .isInstanceOf(BusinessValidationException.class)
                .hasMessage(DomainErrorCodes.COMPANY_NAME_NOT_FOUND);

        // assert
        Mockito.verifyNoInteractions(companyMapperServiceSpy, companyValidationServiceMock);
    }

    @Test
    void create() {
        // arrange
        CompanyDTO companyDTO = new CompanyDTO("Atbash");

        // simulate storing to the db. And also check we did not set the id within code.
        AtomicBoolean emptyId = new AtomicBoolean(false);
        Long id = 1L;
        Mockito.when(companyRepositoryMock.save(Mockito.any())).thenAnswer(i -> {
            Company argument = i.getArgument(0);
            emptyId.set(argument.getId() == null);
            argument.setId(id);
            return argument;
        });

        // act
        CompanyDTOWithId result = companyService.create(companyDTO);

        // assert
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.id()).isEqualTo(id);
        Assertions.assertThat(result.name()).isEqualTo(companyDTO.name());

        Assertions.assertThat(emptyId.get()).isTrue();

        Mockito.verify(companyValidationServiceMock).validateCreate(companyDTO);
    }

    @Test
    void deleteByName() {
        // arrange
        String name = "Atbash";
        Company company = new Company();
        company.setId(1L);
        company.setName(name);

        Mockito.when(companyRepositoryMock.findByName(name)).thenReturn(Optional.of(company));

        // act
        companyService.deleteByName(name);

        // assert
        Mockito.verify(companyRepositoryMock).delete(company);
    }

    @Test
    void deleteByName_unknown() {
        // arrange
        String name = "Atbash";

        Mockito.when(companyRepositoryMock.findByName(name)).thenReturn(Optional.empty());

        // act
        companyService.deleteByName(name);

        // assert
        Mockito.verify(companyRepositoryMock, Mockito.never()).delete(Mockito.any());
    }
}