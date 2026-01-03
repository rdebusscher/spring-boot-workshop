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
package be.atbash.demo.spring.rest.builder;

import be.atbash.demo.spring.rest.model.Company;

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
