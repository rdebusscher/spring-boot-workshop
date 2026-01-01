package be.atbash.demo.spring.rest.repository;

import be.atbash.demo.spring.rest.model.Company;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

@DataJpaTest //  Testing Repositories Only in a Spring context
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class CompanyRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CompanyRepository companyRepository;

    @BeforeEach
    void setUp() {
        prepareCompanyData();
    }

    @Test
    void findByName() {

        // act
        Optional<Company> company = companyRepository.findByName("Atbash");

        // assert
        Assertions.assertThat(company).isPresent();
        Assertions.assertThat(company.get().getId()).isEqualTo(2L);  // Will this always be 2?
        // What if we have multiple tests and data is created each time (but database is not recreated / autoincrement is not recreated)
    }

    private void prepareCompanyData() {
        // This is not ideal, we should prepare a database with data in another way. See 'test-data'.
        Company company1 = new Company();
        company1.setName("JUnit");
        entityManager.persist(company1);

        Company company2 = new Company();
        company2.setName("Atbash");
        entityManager.persist(company2);
    }
}