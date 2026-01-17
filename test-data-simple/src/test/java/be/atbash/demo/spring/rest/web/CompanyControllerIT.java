package be.atbash.demo.spring.rest.web;

import be.atbash.demo.spring.rest.dto.CompanyDTO;
import be.atbash.demo.spring.rest.dto.CompanyWithIdDTO;
import be.atbash.demo.spring.rest.exception.DomainErrorCodes;
import be.atbash.demo.spring.rest.jupiter.ExcelDataSet;
import be.atbash.demo.spring.rest.model.Company;
import be.atbash.demo.spring.rest.repository.CompanyRepository;
import be.atbash.demo.spring.rest.web.checks.BusinessValidationMvcResultCheck;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@ExcelDataSet("classpath:/data/EmployeeRepository.xlsx")
class CompanyControllerIT extends AbstractEndpointTest {

    @Autowired
    private CompanyRepository companyRepository;

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
        int beforeCreate = companyRepository.findAll().size();

        // act
        CompanyWithIdDTO companyWithIdDTO = performPost("/company", new CompanyDTO("Test"), CompanyWithIdDTO.class, MockMvcResultMatchers.status().isCreated());

        // assert
        Assertions.assertThat(companyWithIdDTO).isNotNull();
        Assertions.assertThat(companyWithIdDTO.name()).isEqualTo("Test");

        List<Company> companies = companyRepository.findAll();

        // test just we have an additional record
        Assertions.assertThat(companies.size()).isEqualTo(beforeCreate + 1);

        Set<String> companyNames = companies.stream()
                .map(Company::getName)
                .collect(Collectors.toSet());

        Assertions.assertThat(companyNames).containsExactlyInAnyOrder("JUnit", "Atbash", "Test");

    }

    @Test
    void createCompany_duplicateName() throws Exception {

        // arrange
        int beforeCreate = companyRepository.findAll().size();

        // act and assert
        performPost("/company", new CompanyDTO("Atbash"), CompanyWithIdDTO.class,
                new BusinessValidationMvcResultCheck(DomainErrorCodes.COMPANY_NAME_ALREADY_EXISTS, "The company name already exists"));

        // test just we have no additional record
        Assertions.assertThat(companyRepository.findAll().size()).isEqualTo(beforeCreate);


    }

    @Test
    void createCompany_validationIssue() throws Exception {

        // arrange
        int beforeCreate = companyRepository.findAll().size();

        // act
        performPost("/company", new CompanyDTO(""), CompanyWithIdDTO.class, MockMvcResultMatchers.status().isBadRequest());

        // assert
        // no record added
        Assertions.assertThat(companyRepository.findAll().size()).isEqualTo(beforeCreate);
    }

    @Test
    void deleteCompany() throws Exception {
        // arrange

        boolean companyExists = companyRepository.findAll().stream()
                .anyMatch(company -> company.getName().equals("JUnit"));

        Assertions.assertThat(companyExists).isTrue();  // Check if record exist

        // act
        performDelete("/company/JUnit", MockMvcResultMatchers.status().isNoContent());

        // assert

        List<Company> companies = companyRepository.findAll();
        Assertions.assertThat(companies).extracting(Company::getName).containsExactly("Atbash");
    }

    @Test
    void deleteCompany_unknown() throws Exception {

        // arrange
        boolean companyExists = companyRepository.findAll().stream()
                .anyMatch(company -> company.getName().equals("xxx"));

        Assertions.assertThat(companyExists).isFalse();  // Check if record does not exist

        // act
        performDelete("/company/xxx", MockMvcResultMatchers.status().isNoContent());
        // idempotent
    }
}