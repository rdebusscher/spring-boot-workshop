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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

// Helper parent class for Integration tests which helps in calling endpoints.
public abstract class AbstractEndpointTest {

    @Autowired
    private ObjectMapper objectMapper; // The Jackson Object Mapper which is also used by Spring Boot itself.

    @Autowired
    private MockMvc mockMvc;  // Is able to call the endpoint
    // See 'jwt' example for more details

    protected <T> T performPost(String url, Object body, Class<T> responseClass, ResultMatcher expectedStatusResult) throws Exception {
        String content = asRequestBody(body); // Convert the body to a String (JSON) that is POSTed.

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(new HttpHeaders())
                .content(content);
        MvcResult mvcResult = executeRequest(requestBuilder, expectedStatusResult);

        T result = null;
        if (responseClass != Void.class && mvcResult.getResponse().getStatus() < 400) {
            result = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), responseClass);
        }
        return result;
    }

    private MvcResult executeRequest(MockHttpServletRequestBuilder requestBuilder, ResultMatcher expectedStatusResult) throws Exception {
        // See 'database' example for a more advanced version of this method.
        return mockMvc.perform(requestBuilder)
                .andExpect(expectedStatusResult)
                .andReturn();
    }

    private String asRequestBody(Object body) throws JsonProcessingException {
        if (body instanceof String) {
            return body.toString();
        }
        return body != null ? objectMapper.writeValueAsString(body) : "";
    }
}
