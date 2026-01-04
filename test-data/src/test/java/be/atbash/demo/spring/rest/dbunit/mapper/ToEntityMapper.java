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
import be.atbash.demo.spring.rest.dbunit.PropertyMetaModelData;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

/**
 * A mapper where the Java property is another entity.  We construct an instance of the class
 * and set the value from the database as the value of the id property (the property annotated with @Id)
 */
public class ToEntityMapper implements MapperTypeFactory.TypeMapper<Object> {

    private final Class<?> entityClass;
    private final JpaMetaModelHelper jpaMetaModelHelper;

    public ToEntityMapper(Class<?> entityClass, JpaMetaModelHelper jpaMetaModelHelper) {
        this.entityClass = entityClass;
        this.jpaMetaModelHelper = jpaMetaModelHelper;
    }

    @Override
    public Object map(Object value) {
        PropertyMetaModelData idProperty = jpaMetaModelHelper.getIdProperty(entityClass);
        Object entity;
        try {
            entity = entityClass.getDeclaredConstructor().newInstance();
            Field field = idProperty.field();
            field.setAccessible(true);
            Object idValue = MapperTypeFactory.getInstance(jpaMetaModelHelper).getMapper(idProperty.javaType()).map(value);
            field.set(entity, idValue);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        return entity;
    }
}
