package ru.hse.mit.injector;

import java.util.Collections;

import org.junit.jupiter.api.Test;
import ru.hse.mit.injector.testClassesSimple.ClassWithOneInterfaceDependency;
import ru.hse.mit.injector.testClassesSimple.ClassWithoutDependencies;
import ru.hse.mit.injector.testClassesSimple.ClassWithOneClassDependency;
import ru.hse.mit.injector.testClassesSimple.InterfaceImpl;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class TestInjectorSimple {
    private static final String PACKAGE = "ru.hse.mit.injector.testClassesSimple";

    @Test
    public void injectorShouldInitializeClassWithoutDependencies()
            throws Exception {
        Object object = Injector.initialize(withPackage("ClassWithoutDependencies"), Collections.emptyList());
        assertTrue(object instanceof ClassWithoutDependencies);
    }

    @Test
    public void injectorShouldInitializeClassWithOneClassDependency()
            throws Exception {
        Object object = Injector.initialize(
                withPackage("ClassWithOneClassDependency"),
                Collections.singletonList(withPackage("ClassWithoutDependencies"))
        );
        assertTrue(object instanceof ClassWithOneClassDependency);
        ClassWithOneClassDependency instance = (ClassWithOneClassDependency) object;
        assertNotNull(instance.dependency);
    }

    @Test
    public void injectorShouldInitializeClassWithOneInterfaceDependency()
            throws Exception {
        Object object = Injector.initialize(
                withPackage("ClassWithOneInterfaceDependency"),
                Collections.singletonList(withPackage("InterfaceImpl"))
        );
        assertTrue(object instanceof ClassWithOneInterfaceDependency);
        ClassWithOneInterfaceDependency instance = (ClassWithOneInterfaceDependency) object;
        assertTrue(instance.dependency instanceof InterfaceImpl);
    }

    private static String withPackage(String className) {
        return PACKAGE + "." + className;
    }
}
