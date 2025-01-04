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
package be.atbash.demo.spring.rest.config;

import be.atbash.demo.spring.helper.MvcResultChecker;
import be.atbash.demo.spring.rest.exception.DomainErrorCodes;
import be.atbash.demo.spring.rest.web.AbstractEndpointTest;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ProblemDetail;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.net.URI;

@SpringBootTest
@AutoConfigureMockMvc
class GlobalExceptionHandlerIT extends AbstractEndpointTest {

    @Test
    void handleBusinessValidationException() throws Exception {
        // This also tests the configuration of the resource bundle and determining the message.
        // make use of a 'test' endpoint within ExceptionHandlerResource class.
        // See also GlobalExceptionHandlerTest

        // arrange

        MvcResultChecker problemDetailCheck = (mvcResult, objectMapper) -> {
            ProblemDetail problemDetail = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ProblemDetail.class);
            SoftAssertions assertions = new SoftAssertions();  // multiple assertions at once.
            assertions.assertThat(problemDetail).isNotNull();
            assertions.assertThat(problemDetail.getTitle()).isEqualTo("Validation problem");
            assertions.assertThat(problemDetail.getDetail()).isEqualTo("The parameter Years is out of range (Must be between 0 and 30)");
            Assertions.assertThat(problemDetail.getProperties()).isNotNull();
            Assertions.assertThat(problemDetail.getProperties().get("code")).isEqualTo(DomainErrorCodes.PARAMETER_OUT_RANGE);
            assertions.assertThat(problemDetail.getType()).isEqualTo(URI.create("https://api.atbash.be/errors/business-validation-problem"));
            assertions.assertAll();
        };

        // act
        performGet("/error/domain-validation", Void.class
                , MvcResultChecker.fromResultMatcher(MockMvcResultMatchers.status().isBadRequest()) // status code 400
                , problemDetailCheck);  // response body content check.
    }
}