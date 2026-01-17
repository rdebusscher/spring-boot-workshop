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
    private final EmployeeMapperService employeeMapperService;
    private final EmployeeValidationService employeeValidationService;

    public EmployeeService(EmployeeRepository employeeRepository, CompanyValidationService companyValidationService, EmployeeMapperService employeeMapperService, EmployeeValidationService employeeValidationService) {
        this.employeeRepository = employeeRepository;
        this.companyValidationService = companyValidationService;
        this.employeeMapperService = employeeMapperService;
        this.employeeValidationService = employeeValidationService;
    }


    @Transactional(readOnly = true)
    public List<EmployeeWithIdDTO> findAllEmployeesForCompany(String companyName) {
        Company company = companyValidationService.checkValidName(companyName);
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
