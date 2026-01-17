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
