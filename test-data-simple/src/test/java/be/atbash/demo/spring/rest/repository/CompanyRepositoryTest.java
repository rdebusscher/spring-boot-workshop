package be.atbash.demo.spring.rest.repository;

import be.atbash.demo.spring.rest.jupiter.ExcelDataSet;
import be.atbash.demo.spring.rest.jupiter.TestDataExtension;
import be.atbash.demo.spring.rest.model.Company;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@ExtendWith(TestDataExtension.class)
class CompanyRepositoryTest {

    @Autowired
    private CompanyRepository companyRepository;

    @Test
    @ExcelDataSet("classpath:/data/CompanyRepository.xlsx")  // Can be on the method level so that you can use different datasets for ech test method.
    void findByName() {

        // act
        Optional<Company> company = companyRepository.findByName("Atbash");

        // assert
        Assertions.assertThat(company).isPresent();
        Assertions.assertThat(company.get().getId()).isEqualTo(2L);
    }

    // You should also have a test where you try to find a non-existing name and check if yu get an empty Optional.
}