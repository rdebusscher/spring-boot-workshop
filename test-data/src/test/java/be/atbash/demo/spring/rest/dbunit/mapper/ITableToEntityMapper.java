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
import org.dbunit.dataset.Column;
import org.dbunit.dataset.ITable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ITableToEntityMapper {

    private final JpaMetaModelHelper jpaMetaModelHelper;

    public ITableToEntityMapper(JpaMetaModelHelper jpaMetaModelHelper) {
        this.jpaMetaModelHelper = jpaMetaModelHelper;
    }


    public <T> List<T> mapITableToEntity(ITable table, Class<T> clazz, Map<String, PropertyMetaModelData> metaModelDataMap) throws Exception {
        List<T> entities = new ArrayList<>();

        for (int rowIndex = 0; rowIndex < table.getRowCount(); rowIndex++) {
            // each row
            T entity = clazz.getDeclaredConstructor().newInstance();

            for (Column column : table.getTableMetaData().getColumns()) {
                // each column
                String columnName = column.getColumnName();
                Object value = table.getValue(rowIndex, columnName);

                PropertyMetaModelData propertyMetaModelData = metaModelDataMap.get(columnName.toLowerCase(Locale.ENGLISH));
                if (propertyMetaModelData != null) {

                    Field field = propertyMetaModelData.field();
                    field.setAccessible(true);
                    field.set(entity, MapperTypeFactory.getInstance(jpaMetaModelHelper).getMapper(propertyMetaModelData.javaType()).map(value));
                }
                // what if it is null? this means w have missing mapping information in the entity. We used @Basic?
            }

            entities.add(entity);
        }

        return entities;

    }

}
