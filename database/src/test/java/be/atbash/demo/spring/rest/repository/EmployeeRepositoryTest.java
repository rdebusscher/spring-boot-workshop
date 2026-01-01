package be.atbash.demo.spring.rest.repository;

import be.atbash.demo.spring.rest.builder.EmployeeBuilder;
import be.atbash.demo.spring.rest.model.Company;
import be.atbash.demo.spring.rest.model.Employee;
import be.atbash.demo.spring.rest.model.Gender;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDate;
import java.util.List;

@DataJpaTest  //  Testing Repositories Only in a Spring context
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class EmployeeRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private EmployeeRepository employeeRepository;

    @BeforeEach
    void setUp() {
        prepareTestData();
    }

    @Test
    void findAllByCompanyId() {

        // act
        List<Employee> employees = employeeRepository.findAllByCompanyId(1L);
        // What if we have multiple tests and data is created each time (but database is not recreated / autoincrement is not recreated)

        // assert
        Assertions.assertThat(employees).hasSize(2);

    }

    private void prepareTestData() {
        // This is not ideal, we should prepare a database with data in another way. See 'test-data'.
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