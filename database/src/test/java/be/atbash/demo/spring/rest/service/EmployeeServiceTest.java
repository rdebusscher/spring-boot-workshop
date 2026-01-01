package be.atbash.demo.spring.rest.service;

import be.atbash.demo.spring.rest.builder.CompanyBuilder;
import be.atbash.demo.spring.rest.builder.EmployeeBuilder;
import be.atbash.demo.spring.rest.dto.EmployeeWithIdDTO;
import be.atbash.demo.spring.rest.helper.MapperUtil;
import be.atbash.demo.spring.rest.mapper.EmployeeMapperService;
import be.atbash.demo.spring.rest.model.Company;
import be.atbash.demo.spring.rest.model.Employee;
import be.atbash.demo.spring.rest.repository.EmployeeRepository;
import be.atbash.demo.spring.rest.validation.CompanyValidationService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepositoryMock;
    @Mock
    private CompanyValidationService companyValidationServiceMock;
    @Spy
    private final EmployeeMapperService employeeMapperServiceSpy = MapperUtil.getMapper(EmployeeMapperService.class);
    // The real mapper functionality so that we don't need to mock it in the test

    @InjectMocks
    private EmployeeService employeeService;

    @Test
    void findAllEmployeesForCompany() {
        // arrange
        String companyName = "Atbash";
        Company company = new CompanyBuilder()
                .withId(1L)
                .withName(companyName)
                .build();
        Mockito.when(companyValidationServiceMock.checkValidName(companyName)).thenReturn(company);

        Employee employee1 = new EmployeeBuilder()
                .withEmail("john.doe@acme.org")
                .withCompany(company)
                .build();

        Employee employee2 = new EmployeeBuilder()
                .withEmail("jane.doe@acme.org")
                .withCompany(company)
                .build();

        Mockito.when(employeeRepositoryMock.findAllByCompanyId(company.getId())).thenReturn(List.of(employee1, employee2));

        // act
        List<EmployeeWithIdDTO> employees = employeeService.findAllEmployeesForCompany(companyName);

        // assert
        Assertions.assertThat(employees).isNotNull();
        Assertions.assertThat(employees).hasSize(2);
        Assertions.assertThat(employees).extracting(EmployeeWithIdDTO::email).containsExactly("john.doe@acme.org", "jane.doe@acme.org");

        Mockito.verify(companyValidationServiceMock).checkValidName(companyName);
        Mockito.verify(employeeMapperServiceSpy, Mockito.times(2)).asDtoWithId(Mockito.any());  // Check if we use the mapper and not some custom logic
    }
}