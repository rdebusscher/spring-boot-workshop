package be.atbash.demo.spring.rest.dbunit;

import java.lang.reflect.Field;

public record PropertyMetaModelData(String name, Field field, Class<?> javaType, boolean idField) {
}
