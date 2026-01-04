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
package be.atbash.demo.spring.rest.web.checks;

import be.atbash.demo.spring.rest.helper.MvcResultChecker;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.test.web.servlet.MvcResult;

public class BusinessValidationMvcResultCheck implements MvcResultChecker {
    private final String errorCode;
    private final String errorMessage;

    public BusinessValidationMvcResultCheck(String errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    @Override
    public void check(MvcResult mvcResult, ObjectMapper objectMapper) throws Exception {
        Assertions.assertThat(mvcResult.getResponse().getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());

        ProblemDetail problemDetail = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ProblemDetail.class);
        Assertions.assertThat(problemDetail.getType().toString()).isEqualTo("https://api.atbash.be/errors/business-validation-problem");
        Assertions.assertThat(problemDetail.getTitle()).isEqualTo("Validation problem");
        Assertions.assertThat(problemDetail.getDetail()).isEqualTo(errorMessage);
        Assertions.assertThat(problemDetail.getProperties()).containsKey("code");
        Assertions.assertThat(problemDetail.getProperties().get("code")).isEqualTo(errorCode);
    }
}
