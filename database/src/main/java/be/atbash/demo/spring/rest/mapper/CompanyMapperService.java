package be.atbash.demo.spring.rest.mapper;

import be.atbash.demo.spring.rest.dto.CompanyDTO;
import be.atbash.demo.spring.rest.dto.CompanyDTOWithId;
import be.atbash.demo.spring.rest.model.Company;
import org.springframework.stereotype.Component;

@Component
public class CompanyMapperService {

    public CompanyDTOWithId asDTOWithId(Company company) {
        return new CompanyDTOWithId(company.getId(), company.getName());
    }

    public CompanyDTO asDTO(Company company) {
        return new CompanyDTO(company.getName());
    }

    public Company asEntity(CompanyDTO companyDTO) {
        Company company = new Company();
        company.setName(companyDTO.name());
        return company;
    }
}
