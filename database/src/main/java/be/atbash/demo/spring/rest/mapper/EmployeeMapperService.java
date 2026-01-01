package be.atbash.demo.spring.rest.mapper;

import be.atbash.demo.spring.rest.dto.EmployeeWithIdDTO;
import be.atbash.demo.spring.rest.dto.EmployeeWithoutIdDTO;
import be.atbash.demo.spring.rest.model.Employee;
import org.springframework.stereotype.Component;

@Component
public class EmployeeMapperService {

    private final CompanyMapperService companyMapperService;

    public EmployeeMapperService(CompanyMapperService companyMapperService) {
        this.companyMapperService = companyMapperService;
    }


    public EmployeeWithIdDTO asDtoWithId(Employee employee) {
        return new EmployeeWithIdDTO(
                employee.getId(),
                employee.getEmail(),
                employee.getFirstName(),
                employee.getLastName(),
                employee.getHireDate(),
                employee.getGender(),
                companyMapperService.asDTO(employee.getCompany())
        );
    }


    public Employee asEntity(EmployeeWithoutIdDTO employeeWithIdDto) {
        Employee employee = new Employee();

        employee.setEmail(employeeWithIdDto.email());
        employee.setFirstName(employeeWithIdDto.firstName());
        employee.setLastName(employeeWithIdDto.lastName());
        employee.setHireDate(employeeWithIdDto.hireDate());
        employee.setGender(employeeWithIdDto.gender());
        // no need for trying to set or map Company
        return employee;
    }
}
