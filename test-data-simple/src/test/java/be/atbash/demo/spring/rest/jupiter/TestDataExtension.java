package be.atbash.demo.spring.rest.jupiter;

import be.atbash.demo.spring.rest.dbunit.h2.CustomH2DataTypeFactory;
import be.atbash.demo.spring.rest.dbunit.h2.H2DataSourceDatabaseTester;
import org.assertj.core.api.Assertions;
import org.dbunit.IDatabaseTester;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseDataSourceConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.excel.XlsDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.opentest4j.TestAbortedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.sql.DataSource;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Extension for loading test data in the test database for a DataJpaTest Spring boot test.
 * <p/>
 * Add the following on the class level:
 * <code>@ExtendWith(TestDataExtension.class)</code>
 * <p/>
 * And on each method (or the class) you can define the Excel file(s) that needs to be loaded for that method.
 * <code>@ExcelDataSet("classpath:/data/DocumentRepository1.xls")</code>
 */
public class TestDataExtension implements BeforeEachCallback, AfterAllCallback {

    private final Logger logger = LoggerFactory.getLogger(TestDataExtension.class);

    private ApplicationContext applicationContext;

    private ResourceLoader resourceLoader;

    // List of datasets loaded in the previous test
    private final List<IDataSet> loadedDataSets = new ArrayList<>();

    public void initDependencies(ExtensionContext context) {
        if (applicationContext == null) {
            applicationContext = SpringExtension.getApplicationContext(context);
            resourceLoader = applicationContext;

        }
    }

    private DataSource getDataSource() {
        DataSource dataSource;
        Map<String, DataSource> dataSourceMap = applicationContext.getBeansOfType(DataSource.class);
        if (dataSourceMap.size() != 1) {
            // This is the case when your application as multiple dataSources configures. In that case, you need to define which one you need.
            throw new TestAbortedException("Expected exactly one DataSource bean, found " + dataSourceMap.size());
        } else {
            dataSource = dataSourceMap.values().iterator().next();
        }
        return dataSource;
    }

    private static ExcelDataSet getExcelDataSetAnnotation(ExtensionContext context) {
        ExcelDataSet result = context.getRequiredTestMethod().getAnnotation(ExcelDataSet.class);
        if (result == null) {
            result = context.getRequiredTestClass().getAnnotation(ExcelDataSet.class);
        }
        return result;
    }

    private IDataSet loadDataSet(String excelFilePath) throws Exception {
        Resource resource = resourceLoader.getResource(excelFilePath);
        try (InputStream inputStream = resource.getInputStream()) {
            return new XlsDataSet(inputStream);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load data file: " + excelFilePath, e);
        }
    }

    @Override
    public void beforeEach(ExtensionContext extensionContext) throws Exception {
        initDependencies(extensionContext);
        prepareDatabaseWithData(extensionContext);
    }

    @Override
    public void afterAll(ExtensionContext context) throws Exception {
        logger.info("Cleanup database ");
        performActionOnDataSets(DatabaseOperation.DELETE_ALL);

        if (applicationContext != null && applicationContext instanceof AbstractApplicationContext) {
            try {
                IDatabaseConnection connection = applicationContext.getBean("dbunit.IDatabaseConnection", IDatabaseConnection.class);
                connection.close();
            } catch (Exception e) {
                logger.warn("Error closing database connection", e);
            }
        }
    }

    private void prepareDatabaseWithData(ExtensionContext extensionContext) throws Exception {
        logger.info("Cleanup database ");
        performActionOnDataSets(DatabaseOperation.DELETE_ALL);

        // clear the information, no need for it anymore
        loadedDataSets.clear();

        // Load the Excel files
        loadedDataSets.addAll(loadDataSets(extensionContext));

        // Load into database
        performActionOnDataSets(DatabaseOperation.INSERT);

    }

    private void performActionOnDataSets(DatabaseOperation operation) {
        loadedDataSets.forEach(dataset -> {
            IDatabaseTester databaseTester = getDatabaseTester();
            databaseTester.setDataSet(dataset);
            databaseTester.setSetUpOperation(operation);
            try {
                databaseTester.onSetup();
            } catch (Throwable e) {
                Assertions.fail("Exception during action on dataSets ", e);
            }
        });
    }

    private List<IDataSet> loadDataSets(ExtensionContext extensionContext) throws Exception {
        List<IDataSet> result = new ArrayList<>();

        ExcelDataSet excelDataSet = getExcelDataSetAnnotation(extensionContext);
        if (excelDataSet != null) {
            String[] excelFiles = excelDataSet.value();
            if (excelFiles.length == 0) {
                logger.warn("Empty @ExcelDataSet found for {}", extensionContext.getRequiredTestMethod());
            } else {
                for (String excelFilePath : excelFiles) {
                    logger.info("Loading data from {} into database", excelFilePath);
                    IDataSet dataSet = loadDataSet(excelFilePath);
                    result.add(dataSet);
                }
            }
        } else {
            logger.warn("No @ExcelDataSet found for {}", extensionContext.getRequiredTestMethod());
        }
        return result;
    }

    private IDatabaseTester getDatabaseTester() {
        // We can't reuse the IDatabaseTester between different test methods.
        DataSource dataSource = getDataSource();

        try {
            String schemaName;
            try (Connection connection = dataSource.getConnection()) {
                schemaName = connection.getSchema();
            }

            return new H2DataSourceDatabaseTester(dataSource, schemaName);
        } catch (SQLException e) {

            throw new TestAbortedException("Error creating the IDatabaseTester", e);
        }


    }
}