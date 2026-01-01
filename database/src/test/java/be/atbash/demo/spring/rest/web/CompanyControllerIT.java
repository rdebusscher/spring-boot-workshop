package be.atbash.demo.spring.rest.web;

import be.atbash.demo.spring.rest.builder.EmployeeBuilder;
import be.atbash.demo.spring.rest.dto.CompanyDTO;
import be.atbash.demo.spring.rest.dto.CompanyDTOWithId;
import be.atbash.demo.spring.rest.model.Company;
import be.atbash.demo.spring.rest.model.Employee;
import be.atbash.demo.spring.rest.model.Gender;
import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@SpringBootTest // Start application in 'testing mode'
@AutoConfigureMockMvc // have MockMvc available for calling endpoints.
@Transactional // Needed because we use the EntityManager
class CompanyControllerIT extends AbstractEndpointTest {

    @Autowired
    private EntityManager entityManager;  // Can't use TestEntityManager here since we don't use @DataJpaTest

    @BeforeEach
    void setUp() {
        prepareCompanyData();
    }

    @Test
    void getCompanyByName() throws Exception {

        // act
        CompanyDTOWithId companyDTOWithId = performGet("/company/Atbash", CompanyDTOWithId.class, MockMvcResultMatchers.status().isOk());

        // assert
        Assertions.assertThat(companyDTOWithId).isNotNull();
        //Assertions.assertThat(companyDTOWithId.id()).isEqualTo(2L);  Since @BeforeEach is executed multiple time (once for each test), we can't known the id exactly
        Assertions.assertThat(companyDTOWithId.name()).isEqualTo("Atbash");
    }

    @Test
    void getCompanyByName_unknown() throws Exception {

        // act
        performGet("/company/something", CompanyDTOWithId.class, MockMvcResultMatchers.status().isBadRequest());

        // assert
    }

    @Test
    void createCompany() throws Exception {

        // arrange
        int beforeCreate = entityManager.createQuery("SELECT c FROM Company c").getResultList().size();

        // act
        CompanyDTOWithId companyDTOWithId = performPost("/company", new CompanyDTO("Test"), CompanyDTOWithId.class, MockMvcResultMatchers.status().isCreated());

        // assert
        Assertions.assertThat(companyDTOWithId).isNotNull();
        //Assertions.assertThat(companyDTOWithId.id()).isEqualTo(3L);  Since @BeforeEach is executed multiple time (once for each test), we can't know the id exactly
        Assertions.assertThat(companyDTOWithId.name()).isEqualTo("Test");

        List<Company> companies = entityManager.createQuery("SELECT c FROM Company c", Company.class).getResultList();
        Assertions.assertThat(companies).hasSize(beforeCreate + 1);

        Set<String> companyNames = companies.stream().map(Company::getName).collect(Collectors.toSet());
        Assertions.assertThat(companyNames).contains("Test");
        Assertions.assertThat(companyNames).containsExactlyInAnyOrder("JUnit", "Atbash", "Test");

    }

    @Test
    void deleteCompany() throws Exception {

        // arrange
        int recordCount = entityManager.createQuery("SELECT c FROM Company c WHERE c.name = 'JUnit'", Company.class).getResultList().size();
        Assertions.assertThat(recordCount).isEqualTo(1);  // Check if record exist

        // act
        performDelete("/company/JUnit", MockMvcResultMatchers.status().isNoContent());

        // assert

        // Fails because the transaction is still open.  But we need the transaction for setting up the test data.
        recordCount = entityManager.createQuery("SELECT c FROM Company c WHERE c.name = 'JUnit'", Company.class).getResultList().size();
        Assertions.assertThat(recordCount).isEqualTo(0);

    }

    private void prepareCompanyData() {
        Company company1 = new Company();
        company1.setName("JUnit");
        entityManager.persist(company1);

        Company company2 = new Company();
        company2.setName("Atbash");
        entityManager.persist(company2);

        Employee employee1 = new EmployeeBuilder()
                .withFirstName("John")
                .withLastName("Doe")
                .withEmail("john.doe@acme.org")
                .withHireDate(LocalDate.of(2020, 1, 1))
                .withGender(Gender.MALE)
                .withCompany(company1)
                .build();

        Employee employee2 = new EmployeeBuilder()
                .withFirstName("Jane")
                .withLastName("Doe")
                .withEmail("jane.doe@acme.org")
                .withHireDate(LocalDate.of(2020, 1, 1))
                .withGender(Gender.FEMALE)
                .withCompany(company1)
                .build();

        Employee employee3 = new EmployeeBuilder()
                .withFirstName("Rudy")
                .withLastName("De Busscher")
                .withEmail("info@atbash.be")
                .withHireDate(LocalDate.of(2018, 1, 1))
                .withGender(Gender.MALE)
                .withCompany(company2)
                .build();

        entityManager.persist(employee1);
        entityManager.persist(employee2);
        entityManager.persist(employee3);
    }
}