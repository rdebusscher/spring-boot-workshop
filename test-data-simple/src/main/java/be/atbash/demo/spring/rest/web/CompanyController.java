/*
 * Copyright 2024 Rudy De Busscher (https://www.atbash.be)
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
package be.atbash.demo.spring.rest.web;

import be.atbash.demo.spring.rest.dto.CompanyDTO;
import be.atbash.demo.spring.rest.dto.CompanyWithIdDTO;
import be.atbash.demo.spring.rest.dto.EmployeeWithIdDTO;
import be.atbash.demo.spring.rest.service.CompanyService;
import be.atbash.demo.spring.rest.service.EmployeeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
public class CompanyController {

    private final CompanyService companyService;
    private final EmployeeService employeeService;

    public CompanyController(CompanyService companyService, EmployeeService employeeService) {
        this.companyService = companyService;
        this.employeeService = employeeService;
    }

    @GetMapping("/company/{name}")
    public ResponseEntity<CompanyWithIdDTO> getCompanyByName(@PathVariable("name") String name) {
        return ResponseEntity.ok(companyService.getByName(name));
    }

    @GetMapping("/company")
    public ResponseEntity<List<CompanyWithIdDTO>> getAllCompanies() {
        return ResponseEntity.ok(companyService.getAll());
    }

    @PostMapping("/company")
    public ResponseEntity<CompanyWithIdDTO> createCompany(@RequestBody CompanyDTO companyDTO) {
        CompanyWithIdDTO result = companyService.create(companyDTO);
        return ResponseEntity.created(URI.create("/company/" + result.id())).body(result);
    }

    @DeleteMapping("/company/{name}")
    public ResponseEntity<Void> deleteCompany(@PathVariable("name") String name) {
        companyService.deleteByName(name);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/company/{name}/employee")
    public ResponseEntity<List<EmployeeWithIdDTO>> getEmployeesByCompanyByName(@PathVariable("name") String name) {
        return ResponseEntity.ok(employeeService.findAllEmployeesForCompany(name));
    }

}

