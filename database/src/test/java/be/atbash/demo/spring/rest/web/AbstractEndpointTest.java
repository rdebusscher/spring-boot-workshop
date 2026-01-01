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
package be.atbash.demo.spring.rest.web;

import be.atbash.demo.spring.rest.helper.MvcResultChecker;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.util.ExceptionCollector;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

// Helper parent class for Integration tests which helps calling endpoints.
public abstract class AbstractEndpointTest {

    @Autowired
    private ObjectMapper objectMapper; // The Jackson Object Mapper which is also used by Spring Boot itself.

    @Autowired
    private MockMvc mockMvc;  // Is able to call the endpoint
    // See 'jwt' example for more details

    protected <T> T performGet(String url, Class<T> responseClass, ResultMatcher... matchers) throws Exception {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(url)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(new HttpHeaders());
        MvcResult mvcResult = executeRequest(requestBuilder, matchers);

        T result = null;
        if (responseClass != Void.class && mvcResult.getResponse().getStatus() < 400) {
            result = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), responseClass);
        }
        return result;
    }

    protected <T> T performGet(String url, Class<T> responseClass, MvcResultChecker... checkers) throws Exception {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(url)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(new HttpHeaders());
        MvcResult mvcResult = executeRequest(requestBuilder, new ResultMatcher[]{});

        ExceptionCollector exceptionCollector = new ExceptionCollector();
        for (MvcResultChecker checker : checkers) {
            exceptionCollector.execute(() -> checker.check(mvcResult, objectMapper));
        }
        exceptionCollector.assertEmpty();

        T result = null;
        if (responseClass != Void.class) { // We don't check here on status so that we return the response in any case.
            result = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), responseClass);
        }
        return result;
    }

    protected <T> T performPost(String url, Object body, Class<T> responseClass, ResultMatcher... matchers) throws Exception {
        String content = asRequestBody(body); // Convert the body to a String (Json) that is POSTed.

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(new HttpHeaders())
                .content(content);
        MvcResult mvcResult = executeRequest(requestBuilder, matchers);

        T result = null;
        if (responseClass != Void.class && mvcResult.getResponse().getStatus() < 400) {
            result = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), responseClass);
        }
        return result;
    }

    protected <T> T performPost(String url, Object body, Class<T> responseClass, MvcResultChecker... checkers) throws Exception {
        String content = asRequestBody(body); // Convert the body to a String (Json) that is POSTed.

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(new HttpHeaders())
                .content(content);
        MvcResult mvcResult = executeRequest(requestBuilder, new ResultMatcher[]{});

        ExceptionCollector exceptionCollector = new ExceptionCollector();
        for (MvcResultChecker checker : checkers) {
            exceptionCollector.execute(() -> checker.check(mvcResult, objectMapper));
        }
        exceptionCollector.assertEmpty();

        T result = null;
        if (responseClass != Void.class) { // We don't check here on status so that we return the response in any case.
            result = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), responseClass);
        }
        return result;
    }

    protected void performDelete(String url, ResultMatcher... matchers) throws Exception {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete(url)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(new HttpHeaders());
        executeRequest(requestBuilder, matchers);
    }

    protected void performDelete(String url, MvcResultChecker... checkers) throws Exception {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete(url)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(new HttpHeaders());
        MvcResult mvcResult = executeRequest(requestBuilder, new ResultMatcher[]{});

        ExceptionCollector exceptionCollector = new ExceptionCollector();
        for (MvcResultChecker checker : checkers) {
            exceptionCollector.execute(() -> checker.check(mvcResult, objectMapper));
        }
        exceptionCollector.assertEmpty();

    }

    private MvcResult executeRequest(MockHttpServletRequestBuilder requestBuilder, ResultMatcher[] matchers) throws Exception {
        // See 'database' example for a more advanced version of this method.
        return mockMvc.perform(requestBuilder)
                .andExpectAll(matchers)
                .andReturn();
    }

    private String asRequestBody(Object body) throws JsonProcessingException {
        if (body instanceof String) {
            return body.toString();
        }
        return body != null ? objectMapper.writeValueAsString(body) : "";
    }
}
