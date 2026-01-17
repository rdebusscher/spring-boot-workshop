package be.atbash.demo.spring.rest.mapper;

import be.atbash.demo.spring.rest.dto.CompanyDTO;
import be.atbash.demo.spring.rest.dto.EmployeeWithIdDTO;
import be.atbash.demo.spring.rest.dto.EmployeeWithoutIdDTO;
import be.atbash.demo.spring.rest.helper.MapperUtil;
import be.atbash.demo.spring.rest.model.Company;
import be.atbash.demo.spring.rest.model.Employee;
import be.atbash.demo.spring.rest.model.Gender;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

class EmployeeMapperServiceTest {

    // This always gets a fully functional mapper including the dependencies. As long as the mapper Service only
    // depends on other mappers (as it should be the case)
    private final EmployeeMapperService employeeMapperService = MapperUtil.getMapper(EmployeeMapperService.class);

    @Test
    void asDtoWithId() {
        // arrange
        Employee employee = new Employee();
        employee.setId(11L);
        employee.setEmail("john.doe@acme.org");
        employee.setFirstName("John");
        employee.setLastName("Doe");
        employee.setHireDate(LocalDate.of(2020, 1, 1));
        employee.setGender(Gender.MALE);

        Company atbash = new Company();
        atbash.setId(1L);
        atbash.setName("Atbash");
        employee.setCompany(atbash);

        // act
        EmployeeWithIdDTO employeeWithIdDto = employeeMapperService.asDtoWithId(employee);

        // assert
        Assertions.assertThat(employeeWithIdDto).isNotNull();
        Assertions.assertThat(employeeWithIdDto.id()).isEqualTo(employee.getId());
        Assertions.assertThat(employeeWithIdDto.email()).isEqualTo(employee.getEmail());
        Assertions.assertThat(employeeWithIdDto.firstName()).isEqualTo(employee.getFirstName());
        Assertions.assertThat(employeeWithIdDto.lastName()).isEqualTo(employee.getLastName());
        Assertions.assertThat(employeeWithIdDto.hireDate()).isEqualTo(employee.getHireDate());
        Assertions.assertThat(employeeWithIdDto.gender()).isEqualTo(employee.getGender());
        Assertions.assertThat(employeeWithIdDto.company()).isNotNull();
        Assertions.assertThat(employeeWithIdDto.company().name()).isEqualTo(employee.getCompany().getName());

    }

    @Test
    void asEntity() {
        // arrange
        EmployeeWithoutIdDTO dto = new EmployeeWithoutIdDTO( "john.doe@acme.org", "John", "Doe", LocalDate.of(2020, 1, 1), Gender.MALE, new CompanyDTO("Atbash"));

        // act
        Employee employee = employeeMapperService.asEntity(dto);

        // assert
        Assertions.assertThat(employee).isNotNull();
        Assertions.assertThat(employee.getId()).isNull();
        Assertions.assertThat(employee.getEmail()).isEqualTo(dto.email());
        Assertions.assertThat(employee.getFirstName()).isEqualTo(dto.firstName());
        Assertions.assertThat(employee.getLastName()).isEqualTo(dto.lastName());
        Assertions.assertThat(employee.getHireDate()).isEqualTo(dto.hireDate());
        Assertions.assertThat(employee.getGender()).isEqualTo(dto.gender());
        Assertions.assertThat(employee.getCompany()).isNull();
    }
}