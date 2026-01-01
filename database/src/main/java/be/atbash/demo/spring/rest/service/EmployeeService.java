package be.atbash.demo.spring.rest.service;

import be.atbash.demo.spring.rest.dto.EmployeeWithIdDTO;
import be.atbash.demo.spring.rest.dto.EmployeeWithoutIdDTO;
import be.atbash.demo.spring.rest.mapper.EmployeeMapperService;
import be.atbash.demo.spring.rest.model.Company;
import be.atbash.demo.spring.rest.model.Employee;
import be.atbash.demo.spring.rest.repository.EmployeeRepository;
import be.atbash.demo.spring.rest.validation.CompanyValidationService;
import be.atbash.demo.spring.rest.validation.EmployeeValidationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final CompanyValidationService companyValidationService;
    private final EmployeeValidationService employeeValidationService;
    private final EmployeeMapperService employeeMapperService;

    public EmployeeService(EmployeeRepository employeeRepository, CompanyValidationService companyValidationService, EmployeeValidationService employeeValidationService, EmployeeMapperService employeeMapperService) {
        this.employeeRepository = employeeRepository;
        this.companyValidationService = companyValidationService;
        this.employeeValidationService = employeeValidationService;
        this.employeeMapperService = employeeMapperService;
    }


    @Transactional(readOnly = true)
    public List<EmployeeWithIdDTO> findAllEmployeesForCompany(String name) {
        Company company = companyValidationService.checkValidName(name);
        return employeeRepository.findAllByCompanyId(company.getId())
                .stream()
                .map(employeeMapperService::asDtoWithId)
                .toList();

    }


    public EmployeeWithIdDTO create(EmployeeWithoutIdDTO dto) {
        Company company = employeeValidationService.validateCreate(dto);
        Employee entity = employeeMapperService.asEntity(dto);
        entity.setCompany(company);
        entity = employeeRepository.save(entity);
        return employeeMapperService.asDtoWithId(entity);
    }
}
