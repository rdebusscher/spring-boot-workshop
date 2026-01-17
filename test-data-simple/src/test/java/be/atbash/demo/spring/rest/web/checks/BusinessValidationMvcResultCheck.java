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
