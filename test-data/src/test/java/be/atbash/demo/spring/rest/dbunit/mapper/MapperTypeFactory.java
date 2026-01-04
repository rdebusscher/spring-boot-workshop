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

import be.atbash.demo.spring.rest.dbunit.JpaMetaModelHelper;
import org.apache.poi.ss.formula.functions.T;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * Factory class that keeps the collection of Mappers.
 */
class MapperTypeFactory {

    static MapperTypeFactory INSTANCE;

    private final Map<Class<?>, TypeMapper<?>> mappers
            = Map.of(Long.class, new ToLongMapper()
            , String.class, new ToStringMapper()
            , LocalDate.class, new ToLocalDateMapper());
    // We could define some more types than these basic ones.

    private final JpaMetaModelHelper jpaMetaModelHelper;
    private final Map<Class<?>, TypeMapper<?>> enumMappers = new HashMap<>();
    private final Map<Class<?>, TypeMapper<?>> entityMappers = new HashMap<>();

    private MapperTypeFactory(JpaMetaModelHelper jpaMetaModelHelper) {
        this.jpaMetaModelHelper = jpaMetaModelHelper;
    }

    public static MapperTypeFactory getInstance(JpaMetaModelHelper jpaMetaModelHelper) {
        if (INSTANCE == null) {
            INSTANCE = new MapperTypeFactory(jpaMetaModelHelper);
        }
        return INSTANCE;
    }

    @SuppressWarnings("unchecked")
    TypeMapper<T> getMapper(Class<?> fieldType) {
        if (fieldType.isEnum()) {  // Enum field
            return (TypeMapper<T>) enumMappers.computeIfAbsent(fieldType, f -> new ToEnumMapper<>((Class<? extends Enum<?>>) f));
        }
        if (jpaMetaModelHelper.isEntity(fieldType)) {  // Another Entity (@ManyToOne)
            return (TypeMapper<T>) entityMappers.computeIfAbsent(fieldType, f-> new ToEntityMapper(f, jpaMetaModelHelper));
        }
        return (TypeMapper<T>) mappers.get(fieldType);  // The 'simple' mappers
    }

    @FunctionalInterface
    public interface TypeMapper<T> {
        // Convert from JDBCType value to Java Type T
        T map(Object value);
    }
}
