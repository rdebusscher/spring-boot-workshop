package be.atbash.demo.spring.rest.repository;

import be.atbash.demo.spring.rest.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    // Hibernate query explicitly defined to make it easier to read

    @Query("SELECT e FROM Employee e WHERE e.company.id = :companyId")
    List<Employee> findAllByCompanyId(@Param("companyId") Long companyId);

    @Query("SELECT e FROM Employee e WHERE e.email = :email AND e.company.id = :companyId")
    Optional<Employee> findByEmail(@Param("email") String email, @Param("companyId") Long companyId);

    @Query("DELETE FROM Employee e WHERE e.company.id = :companyId")
    @Modifying  // required, mainly to make it explicit and that no entities should be returned
    void deleteAllByCompanyId(@Param("companyId") Long companyId);

}
