package be.atbash.demo.spring.rest.builder;

import be.atbash.demo.spring.rest.model.Company;

/**
 * This is a helper for testing so that we can create instances of the Company class in a fluent way.
 */
public class CompanyBuilder {

    private final Company company;

    public CompanyBuilder() {
        this.company = new Company();
    }

    public CompanyBuilder withId(Long id) {
        company.setId(id);
        return this;
    }

    public CompanyBuilder withName(String name) {
        company.setName(name);
        return this;
    }

    public Company build() {
        return company;
    }
}
