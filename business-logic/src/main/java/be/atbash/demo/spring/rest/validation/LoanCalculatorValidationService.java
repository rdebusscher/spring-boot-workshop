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
package be.atbash.demo.spring.rest.validation;

public interface LoanCalculatorValidationService {
    // Although many Spring users are used to work with Interfaces, there is actually no need for this.
    // And from the Java point of view, you should not introduce it since you will only have 1 implementation.
    // See the 'no-interface' example for a version without interfaces.

    void validateParameters(double amount, int years);
}
