package ru.hse.mit.injector;

import org.jetbrains.annotations.NotNull;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Injector {
    /**
     * Create and initialize object of `rootClassName` class using classes from
     * `implementationClassNames` for concrete dependencies.
     */
    private static List<Class<?>> implementationClassesList;
    private static HashMap<Class, Object> createdClassesMap;

    @NotNull
    public static Object initialize(String rootClassName, List<String> implementationClassNames) throws Exception {
        Class rootClass = Class.forName(rootClassName);
        implementationClassesList = new ArrayList<>();
        createdClassesMap = new HashMap<>();
        for (String className : implementationClassNames) {
            implementationClassesList.add(Class.forName(className));
        }
        if (!implementationClassNames.contains(rootClassName)) {
            implementationClassesList.add(rootClass);
        }
        return getInstance(rootClass);
    }

    private static Object getInstance(Class<?> clazz) throws InvocationTargetException, InstantiationException, IllegalAccessException, ImplementationNotFoundException, AmbiguousImplementationException, InjectionCycleException {
        Constructor constructor = clazz.getDeclaredConstructors()[0];
        Class<?>[] parametersTypes = constructor.getParameterTypes();
        List<Object> parametersList = new ArrayList<>();
        for (Class<?> parameterType : parametersTypes) {
            List<Class> actualImplementationsList = implementationClassesList.stream().filter(parameterType::isAssignableFrom).collect(Collectors.toList());
            if (actualImplementationsList.size() == 0) {
                throw new ImplementationNotFoundException();
            }
            if (actualImplementationsList.size() > 1) {
                throw new AmbiguousImplementationException();
            }
            Class actualClass = actualImplementationsList.get(0);
            if (!createdClassesMap.containsKey(actualClass)) {
                createdClassesMap.put(actualClass, null);
                parametersList.add(getInstance(actualClass));
            } else if (createdClassesMap.get(actualClass) == null) {
                throw new InjectionCycleException();
            } else {
                parametersList.add(createdClassesMap.get(actualClass));
            }
        }
        Object instance = constructor.newInstance(parametersList.toArray());
        createdClassesMap.put(clazz, instance);
        return instance;
    }
}