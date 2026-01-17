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
