/*
 * Copyright 2024-2025 Rudy De Busscher (https://www.atbash.be)
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
package be.atbach.demo.spring.rest.web;

import be.atbach.demo.spring.rest.ApplicationConstants;
import be.atbach.demo.spring.rest.exception.BusinessValidationException;
import be.atbach.demo.spring.rest.exception.DomainErrorCodes;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ExceptionHandlerResource {

    @GetMapping("/error/domain-validation")
    public void domainValidation() {
        throw new BusinessValidationException(DomainErrorCodes.PARAMETER_OUT_RANGE, new Object[]{"Years", 0, ApplicationConstants.MAX_YEARS_ALLOWED});
    }
}
