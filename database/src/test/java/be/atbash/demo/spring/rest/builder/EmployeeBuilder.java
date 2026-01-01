package be.atbash.demo.spring.rest.builder;

import be.atbash.demo.spring.rest.model.Company;
import be.atbash.demo.spring.rest.model.Employee;
import be.atbash.demo.spring.rest.model.Gender;

import java.time.LocalDate;

/**
 * A typical builder pattern for the Employee entity. You don't need this in production code as you have the mappers
 * and you always get an entity instance from the database or a dto from the endpoint.
 */
public class EmployeeBuilder {

    private final Employee employee;

    public EmployeeBuilder() {
        this.employee = new Employee();
    }

    public EmployeeBuilder withId(Long id) {
        employee.setId(id);
        return this;
    }

    public EmployeeBuilder withEmail(String email) {
        employee.setEmail(email);
        return this;
    }

    public EmployeeBuilder withFirstName(String firstName) {
        employee.setFirstName(firstName);
        return this;
    }

    public EmployeeBuilder withLastName(String lastName) {
        employee.setLastName(lastName);
        return this;
    }

    public EmployeeBuilder withHireDate(LocalDate hireDate) {
        employee.setHireDate(hireDate);
        return this;
    }

    public EmployeeBuilder withGender(Gender gender) {
        employee.setGender(gender);
        return this;
    }

    public EmployeeBuilder withCompany(Company company) {
        employee.setCompany(company);
        return this;
    }

    public Employee build() {
        return employee;
    }
}
