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

import be.atbash.demo.spring.rest.dbunit.mapper.ITableToEntityMapper;
import jakarta.inject.Provider;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ITable;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class TestDataHelper {

    @Autowired
    private JpaMetaModelHelper jpaMetaModelHelper;

    @Autowired
    protected Provider<IDatabaseConnection> databaseConnectionProvider;
    // This is added to the Spring Context by the TestDataExtension.

    protected int getRowCountFor(String tableName) throws Exception {
        return databaseConnectionProvider.get().createTable(tableName).getRowCount();
    }

    /**
     * Retrieves all records from the specified table and convert them to instances of the rowType class.
     *
     * @param tableName the database table to query
     * @param rowType   The class used for the mapping
     * @param <T>       Type of Java instances
     * @return List of database records as Java instances.
     * @throws Exception Any Exception in case something goes wrong or doesn't exist.
     */
    protected <T> List<T> getRowsFor(String tableName, Class<T> rowType) throws Exception {
        // Use DBUnit to retrieve the records from the table
        ITable table = databaseConnectionProvider.get().createTable(tableName);
        // our little mapping tool.
        return new ITableToEntityMapper(jpaMetaModelHelper).mapITableToEntity(table, rowType, jpaMetaModelHelper.getMetaModel(rowType));
    }

    /**
     * Retrieves the records defined by the query and convert them to instances of the rowType class.
     *
     * @param query   The SQL query to be executed.
     * @param rowType The class used for the mapping
     * @param <T>     Type of Java instances
     * @return List of records as Java instances.
     * @throws Exception Any Exception in case something goes wrong or doesn't exist.
     */
    protected <T> List<T> performQuery(String query, Class<T> rowType) throws Exception {
        ITable table = databaseConnectionProvider.get().createQueryTable("tmp", query);
        return new ITableToEntityMapper(jpaMetaModelHelper).mapITableToEntity(table, rowType, jpaMetaModelHelper.getMetaModel(rowType));
    }
}
