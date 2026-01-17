package be.atbash.demo.spring.rest.repository;

import be.atbash.demo.spring.rest.jupiter.ExcelDataSet;
import be.atbash.demo.spring.rest.model.Employee;
import be.atbash.demo.spring.rest.test.UpdatingRepositoryTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

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

        List<Employee> employees = employeeRepository.findAll();
        // We can assume the findAll works correctly as it is from the Hibernate library.

        Assertions.assertThat(employees).extracting(e -> e.getCompany().getId()).containsExactly(-2L);
    }
}
