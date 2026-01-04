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

import org.dbunit.DataSourceDatabaseTester;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.IDatabaseConnection;
import org.opentest4j.TestAbortedException;

import javax.sql.DataSource;

/**
 * Fix the bug in DataSourceDatabaseTester where a new connection is created each time and thus looses
 * the configured DataTypeFactory.
 */
public class H2DataSourceDatabaseTester extends DataSourceDatabaseTester {

    private final IDatabaseConnection connection;

    public H2DataSourceDatabaseTester(DataSource dataSource, String schema) {
        super(dataSource, schema);
        try {
            connection = super.getConnection();
            connection.getConfig().setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new CustomH2DataTypeFactory());
            connection.getConfig().setProperty(DatabaseConfig.FEATURE_ALLOW_EMPTY_FIELDS, Boolean.TRUE);

        } catch (Exception e) {
            throw new TestAbortedException("Error creating the H2DataSourceDatabaseTester", e);
        }
    }

    @Override
    public IDatabaseConnection getConnection() throws Exception {
        return connection;
    }
}
