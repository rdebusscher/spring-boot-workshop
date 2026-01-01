package be.atbash.demo.spring.rest.helper;

import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class MapperUtil {

    private static final Map<Class<?>, Object> MAPPER_CACHE = new HashMap<>();

    public static <T> T getMapper(Class<T> mapperClass) {
        if (MAPPER_CACHE.containsKey(mapperClass)) {
            return (T) MAPPER_CACHE.get(mapperClass);

        }

        T mapper = createMapper(mapperClass);
        MAPPER_CACHE.put(mapperClass, mapper);
        return mapper;
    }

    private static <T> T createMapper(Class<T> mapperClass) {

        List<Object> dependencies = resolveDependencies(mapperClass);
        // there should always be just one constructor
        Constructor<?> constructor = mapperClass.getDeclaredConstructors()[0];
        T mapper;
        try {
            mapper = (T) constructor.newInstance(dependencies.toArray(new Object[0]));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return mapper;
    }

    private static <T> List<Object> resolveDependencies(Class<T> mapperClass) {
        List<Object> dependencies = new ArrayList<>();
        for (Parameter parameter : mapperClass.getDeclaredConstructors()[0].getParameters()) {
            Class<?> dependencyClass = parameter.getType();
            dependencies.add(getMapper(dependencyClass));
        }
        return dependencies;
    }
}
