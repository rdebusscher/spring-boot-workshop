package be.atbash.demo.spring.rest.repository;

import be.atbash.demo.spring.rest.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, Long> {

    // Hibernate query explicitly defined to make it easier to read
    @Query("SELECT c FROM Company c WHERE c.name = :name")
    Optional<Company> findByName(@Param("name") String name);
}
