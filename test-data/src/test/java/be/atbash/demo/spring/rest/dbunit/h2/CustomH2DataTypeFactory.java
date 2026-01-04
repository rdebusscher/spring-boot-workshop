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
package be.atbash.demo.spring.rest.dbunit.h2;

import org.dbunit.dataset.datatype.DataType;
import org.dbunit.dataset.datatype.DataTypeException;
import org.dbunit.ext.h2.H2DataTypeFactory;

import java.sql.Types;

public class CustomH2DataTypeFactory extends H2DataTypeFactory {

    @Override
    public DataType createDataType(int sqlType, String sqlTypeName) throws DataTypeException {

        // Since Hibernate 6 returns some sqlTypes DBUnit can't handle.
        if (sqlType == Types.OTHER && sqlTypeName.startsWith("ENUM")) {
            return DataType.VARCHAR;
        }

        // Delegate to the default behavior for other data types
        return super.createDataType(sqlType, sqlTypeName);
    }
}
