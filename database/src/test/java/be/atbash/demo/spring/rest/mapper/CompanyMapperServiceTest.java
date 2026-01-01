package be.atbash.demo.spring.rest.mapper;

import be.atbash.demo.spring.rest.dto.CompanyDTO;
import be.atbash.demo.spring.rest.dto.CompanyDTOWithId;
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
        CompanyDTOWithId companyDTOWithId = companyMapperService.asDTOWithId(company);

        // assert
        Assertions.assertThat(companyDTOWithId).isNotNull();
        Assertions.assertThat(companyDTOWithId.id()).isEqualTo(1L);
        Assertions.assertThat(companyDTOWithId.name()).isEqualTo("Atbash");

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