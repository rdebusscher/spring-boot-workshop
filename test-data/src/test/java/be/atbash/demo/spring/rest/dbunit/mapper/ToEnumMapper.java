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

public class ToEnumMapper<T extends Enum<T>> implements TypeMapper<T> {
    private final Class<? extends Enum<?>> enumClass;

    public ToEnumMapper(Class<? extends Enum<?>> enumClass) {
        this.enumClass = enumClass;
    }

    @Override
    public T map(Object name) {
        return Enum.valueOf((Class<T>) enumClass, name.toString());
    }
}
