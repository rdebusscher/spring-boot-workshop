package be.atbash.demo.spring.rest.dto;

import be.atbash.demo.spring.rest.model.Gender;

import java.time.LocalDate;

public record EmployeeWithIdDTO(Long id, String email, String firstName, String lastName, LocalDate hireDate, Gender gender, CompanyDTO company) {}