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

import be.atbash.demo.spring.rest.jupiter.ExcelDataSet;
import be.atbash.demo.spring.rest.model.Employee;
import be.atbash.demo.spring.rest.test.UpdatingRepositoryTest;
import org.assertj.core.api.Assertions;
import org.dbunit.dataset.ITable;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigInteger;
import java.util.List;

@ExcelDataSet("classpath:/data/EmployeeRepository.xlsx")
class EmployeeRepositoryTest extends UpdatingRepositoryTest {
    // the UpdatingRepositoryTest brings in a method for running statements within a separate thread and transaction.
    // It also adds methods for reading records from the database through DBUnit and a custom mapping solution.
    @Autowired
    private EmployeeRepository employeeRepository;

    @Test
    void findAllByCompanyId() {

        // act
        List<Employee> employees = employeeRepository.findAllByCompanyId(-1L);

        // assert
        Assertions.assertThat(employees).hasSize(2);

    }

    @Test
    void deleteAllByCompanyId() throws Exception {

        // act
        // In a separate transaction so that changes are visible for DB Unit.
        runInTransaction(() -> employeeRepository.deleteAllByCompanyId(-1L));

        // assert
        // access the database using DBUnit, not using application code.

        List<Employee> employees = getRowsFor("employee", Employee.class);
        Assertions.assertThat(employees).extracting(e -> e.getCompany().getId()).containsExactly(-2L);
        // Not the entire Company instance is mapped into the Employee instance. But since we have the Company Id in te Employee table, th company.id has a value.

        // alternative using DBUnit directly and not map to Java instances.
        // .createTable() creates a Table structure from the database content od that table?
        ITable employee = databaseConnectionProvider.get().createTable("employee");
        Assertions.assertThat(employee.getRowCount()).isEqualTo(1);
        Assertions.assertThat(employee.getValue(0, "company_id")).isEqualTo(BigInteger.valueOf(-2L));

    }
}
