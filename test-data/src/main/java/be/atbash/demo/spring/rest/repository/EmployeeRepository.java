/*
 * Copyright 2024-2026 Rudy De Busscher (https://www.atbash.be)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

    @Query("SELECT e FROM Employee e WHERE e.company.id = :companyId")
    List<Employee> findAllByCompanyId(@Param("companyId") Long companyId);

    @Query("SELECT e FROM Employee e WHERE e.email = :email AND e.company.id = :companyId")
    Optional<Employee> findByEmail(@Param("email") String email, @Param("companyId") Long companyId);

    @Query("DELETE FROM Employee e WHERE e.company.id = :companyId")
    @Modifying
    void deleteAllByCompanyId(@Param("companyId") Long companyId);
}
