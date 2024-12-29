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

import be.atbash.demo.spring.rest.dto.LoanParametersDTO;
import be.atbash.demo.spring.rest.dto.LoanResponseDTO;
import org.assertj.core.api.Assertions;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest // Start application in 'testing mode'
@AutoConfigureMockMvc // have MockMvc available for calling endpoints.
class LoanControllerIT extends AbstractEndpointTest {
    // The idea is to test the application in a manner that is similar to production usage.

    @Test
    void calculateMonthlyAmount() throws Exception {
        // arrange
        LoanParametersDTO parameters = new LoanParametersDTO(10_000.0, 10);

        // act
        LoanResponseDTO loanResponseDTO = performPost("/loan", parameters, LoanResponseDTO.class, MockMvcResultMatchers.status().isOk());

        // assert
        Assertions.assertThat(loanResponseDTO).isNotNull();
        Assertions.assertThat(loanResponseDTO.monthlyAmount()).isEqualTo(116.057, Offset.offset(0.001));
    }

    @Test
    void calculateMonthlyAmount_invalidData() throws Exception {
        // This test fails and will be solved in the 'exception-handling' example.

        // arrange
        LoanParametersDTO parameters = new LoanParametersDTO(-10_000.0, 10);

        // act
        performPost("/loan", parameters, LoanResponseDTO.class, MockMvcResultMatchers.status().isBadRequest());
    }
}