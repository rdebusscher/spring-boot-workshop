package be.atbash.demo.spring.rest.jupiter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation that can be used on a test method or class to indicate the Excel files with data
 * that needs to be loaded by DBUnit.
 * Requires <code>@ExtendWith(TestDataExtension.class)</code> before they have effect.
 * Multiple files can be specified and are loaded by Spring Resource loader so <code>classpath:</code> is supported in the name.
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelDataSet {

    String[] value();
}
