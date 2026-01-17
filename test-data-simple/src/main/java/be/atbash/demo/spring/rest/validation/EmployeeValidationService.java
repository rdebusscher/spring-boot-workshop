package be.atbash.demo.spring.rest.validation;

import be.atbash.demo.spring.rest.dto.EmployeeWithoutIdDTO;
import be.atbash.demo.spring.rest.exception.BusinessValidationException;
import be.atbash.demo.spring.rest.exception.DomainErrorCodes;
import be.atbash.demo.spring.rest.model.Company;
import be.atbash.demo.spring.rest.repository.EmployeeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true, propagation = Propagation.MANDATORY)
// Can only be used from a service that started the transaction
public class EmployeeValidationService {

    private final EmployeeRepository employeeRepository;
    private final CompanyValidationService companyValidationService;

    public EmployeeValidationService(EmployeeRepository employeeRepository, CompanyValidationService companyValidationService) {
        this.employeeRepository = employeeRepository;
        this.companyValidationService = companyValidationService;
    }

    public Company validateCreate(EmployeeWithoutIdDTO dto) {
        Company company = companyValidationService.checkValidName(dto.company().name());
        shouldFailWhenEmailAlreadyInUse(dto.email(), company.getId());
        return company;
    }

    protected void shouldFailWhenEmailAlreadyInUse(String email, Long companyId) {
        employeeRepository.findByEmail(email, companyId)
                .ifPresent(e -> {
                    throw new BusinessValidationException(DomainErrorCodes.EMPLOYEE_EMAIL_ALREADY_IN_USE);
                });
    }
}
