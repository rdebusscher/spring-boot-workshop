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
package be.atbash.demo.spring.rest.dbunit;

import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.data.jpa.mapping.JpaPersistentEntity;
import org.springframework.data.mapping.Association;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.data.mapping.SimpleAssociationHandler;
import org.springframework.data.mapping.SimplePropertyHandler;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * A helper that uses the Hibernate metamodel to map database fields to java properties.
 */
@Component
public class JpaMetaModelHelper {

    private final JpaMetamodelMappingContext metamodelMappingContext;
    private final Map<Class<?>, Map<String, PropertyMetaModelData>> metaModel = new HashMap<>();
    // key is entity class, vaue is the map of Java Properties with the data required for the mapping.

    /**
     * The Helper uses the Spring Boot component that holds the Hibernate Mapping information.
     * @param metamodelMappingContext Context with the Hibernate mapping information.
     */
    public JpaMetaModelHelper(JpaMetamodelMappingContext metamodelMappingContext) {
        this.metamodelMappingContext = metamodelMappingContext;
    }

    public boolean isEntity(Class<?> entityClass) {
        return metamodelMappingContext.getPersistentEntity(entityClass) != null;
    }

    public Map<String, PropertyMetaModelData> getMetaModel(Class<?> entityClass) {
        return metaModel.computeIfAbsent(entityClass, this::determineMetaModel);
    }

    private Map<String, PropertyMetaModelData> determineMetaModel(Class<?> entityClass) {
        Map<String, PropertyMetaModelData> result = new HashMap<>();

        JpaPersistentEntity<?> persistentEntity = metamodelMappingContext.getPersistentEntity(entityClass);
        if (persistentEntity == null) {
            return result;
        }

        // Use callback handler to retrieve the mapping information.
        persistentEntity.doWithProperties(new MyPropertyHandler(result));
        persistentEntity.doWithAssociations(new MyAssociationHandler(result));
        return result;

    }

    public PropertyMetaModelData getIdProperty(Class<?> entityClass) {
        return getMetaModel(entityClass).values()
                .stream()
                .filter(PropertyMetaModelData::idField).findAny().orElseThrow(() -> new IllegalArgumentException("No id property found for " + entityClass.getName()));
    }

    private static class MyPropertyHandler implements SimplePropertyHandler {

        private final Map<String, PropertyMetaModelData> metaModelDataMap;

        public MyPropertyHandler(Map<String, PropertyMetaModelData> metaModelDataMap) {
            this.metaModelDataMap = metaModelDataMap;
        }

        @Override
        public void doWithPersistentProperty(PersistentProperty<?> property) {

            Column columnAnnotation = property.findAnnotation(Column.class);
            boolean isIdProperty = property.isIdProperty();
            if (columnAnnotation != null) {
                metaModelDataMap.put(columnAnnotation.name().toLowerCase(Locale.ENGLISH), new PropertyMetaModelData(property.getName(), property.getField(), property.getType(), isIdProperty));
            } else {
                ///  no Column, so java property name is database field name.
                metaModelDataMap.put(property.getName().toLowerCase(Locale.ENGLISH), new PropertyMetaModelData(property.getName(), property.getField(), property.getType(), isIdProperty));
            }
        }
    }

    private static class MyAssociationHandler implements SimpleAssociationHandler {

        private final Map<String, PropertyMetaModelData> metaModelDataMap;

        public MyAssociationHandler(Map<String, PropertyMetaModelData> metaModelDataMap) {
            this.metaModelDataMap = metaModelDataMap;
        }

        @Override
        public void doWithAssociation(Association<? extends PersistentProperty<?>> association) {
            PersistentProperty<? extends PersistentProperty<?>> property = association.getInverse();
            JoinColumn joinColumnAnnotation = property.findAnnotation(JoinColumn.class);
            if (joinColumnAnnotation != null) {
                metaModelDataMap.put(joinColumnAnnotation.name().toLowerCase(Locale.ENGLISH), new PropertyMetaModelData(property.getName(), property.getField(), property.getType(), false));
            } else {
                // FIXME Is this correct?  But our rule stating that we should be explicit and thus have JoinColumn makes this branch not needed.
                Class<?> type = property.getType();
                metaModelDataMap.put(type.getSimpleName() + "_" + property.getName().toLowerCase(Locale.ENGLISH), new PropertyMetaModelData(property.getName(), property.getField(), type, false));
            }
        }
    }
}
