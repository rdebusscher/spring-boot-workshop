package be.atbash.demo.spring.rest.validation;

import be.atbash.demo.spring.rest.dto.CompanyDTO;
import be.atbash.demo.spring.rest.exception.BusinessValidationException;
import be.atbash.demo.spring.rest.exception.DomainErrorCodes;
import be.atbash.demo.spring.rest.model.Company;
import be.atbash.demo.spring.rest.repository.CompanyRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class CompanyValidationServiceTest {

    @Mock
    private CompanyRepository companyRepositoryMock;

    @InjectMocks
    private CompanyValidationService companyValidationService;

    @Test
    void validateCreate() {
        // arrange
        CompanyDTO company = new CompanyDTO("Atbash");
        Mockito.when(companyRepositoryMock.findByName(company.name())).thenReturn(Optional.empty());

        // act
        companyValidationService.validateCreate(company);

        // assert
    }

    @Test
    void validateCreate_existingName() {
        // arrange
        CompanyDTO company = new CompanyDTO("Atbash");

        Mockito.when(companyRepositoryMock.findByName(company.name())).thenReturn(Optional.of(new Company()));
        // no need to return the actual company instance, we just check on the Optional.isPresent

        // act
        Assertions.assertThatThrownBy(() -> companyValidationService.validateCreate(company))
                .isInstanceOf(BusinessValidationException.class)
                .hasMessage(DomainErrorCodes.COMPANY_NAME_ALREADY_EXISTS);

        // assert
    }
}