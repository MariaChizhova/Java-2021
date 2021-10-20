package ru.hse.mit.injector.testClassesExceptions;

public class A7 implements Interface2 {

    public static boolean wasInitialized = false;

    public A7() {
        assert !wasInitialized : "Too many instances of the same class";
        wasInitialized = true;
    }

    @Override
    public String bar() {
        return "OK";
    }
}