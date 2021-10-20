package ru.hse.mit.injector.testClassesExceptions;

import org.junit.jupiter.api.Assertions;

public class A18 {

    public A18() {
        Assertions.fail("Unnecessary impl should not be created");
    }
}