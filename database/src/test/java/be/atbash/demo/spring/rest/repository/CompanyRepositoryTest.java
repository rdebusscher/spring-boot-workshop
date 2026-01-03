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