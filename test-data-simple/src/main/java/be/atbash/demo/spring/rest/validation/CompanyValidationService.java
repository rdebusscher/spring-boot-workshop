package be.atbash.demo.spring.rest.validation;

import be.atbash.demo.spring.rest.dto.CompanyDTO;
import be.atbash.demo.spring.rest.exception.BusinessValidationException;
import be.atbash.demo.spring.rest.exception.DomainErrorCodes;
import be.atbash.demo.spring.rest.model.Company;
import be.atbash.demo.spring.rest.repository.CompanyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true, propagation = Propagation.MANDATORY) // Can only be used from a service that started the transaction
public class CompanyValidationService {

    private final CompanyRepository companyRepository;

    public CompanyValidationService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    public void validateCreate(CompanyDTO company) {
        companyRepository.findByName(company.name())
                .ifPresent(c -> {
                    throw new BusinessValidationException(DomainErrorCodes.COMPANY_NAME_ALREADY_EXISTS);
                });

    }

    public Company checkValidName(String name) {
        return companyRepository.findByName(name)
                .orElseThrow(() -> new BusinessValidationException(DomainErrorCodes.COMPANY_NAME_NOT_FOUND));
    }
}
