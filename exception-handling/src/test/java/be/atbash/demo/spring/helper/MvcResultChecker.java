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
package be.atbash.demo.spring.helper;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

@FunctionalInterface
public interface MvcResultChecker {

    /**
     * Can be used to validate the response of a REST call. The 'meta data' like status code and header are accessible
     * from the {@link MvcResult} object. And the response body can be converted to a Java instance using the
     * {@link ObjectMapper}.
     *
     * @param mvcResult    the result of the REST call
     * @param objectMapper the Jackson object mapper
     * @throws Exception if something goes wrong or the response is not as expected (Assertion error)
     */
    void check(MvcResult mvcResult, ObjectMapper objectMapper) throws Exception;

    /**
     * Can be used to create a {@link MvcResultChecker} from a {@link ResultMatcher}.
     * @param matcher the {@link ResultMatcher}
     * @return a {@link MvcResultChecker} wrapped around the {@link ResultMatcher}
     */
    static MvcResultChecker fromResultMatcher(ResultMatcher matcher) {
        return (mvcResult, objectMapper) -> {
            matcher.match(mvcResult);
        };
    }
}
