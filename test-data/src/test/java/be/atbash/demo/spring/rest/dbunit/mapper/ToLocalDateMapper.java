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
package be.atbash.demo.spring.rest.dbunit.mapper;

import be.atbash.demo.spring.rest.dbunit.mapper.MapperTypeFactory.TypeMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Date;
import java.time.LocalDate;

public class ToLocalDateMapper implements TypeMapper<LocalDate> {
    private final Logger log = LoggerFactory.getLogger(ToLocalDateMapper.class);

    @Override
    public LocalDate map(Object value) {

        LocalDate result = null;
        if (value instanceof Date date) {
            result = date.toLocalDate();
        } else {
            log.warn("Not implemented : convert value  of type {} to LocalDate", value.getClass());
        }
        return result;
    }
}
