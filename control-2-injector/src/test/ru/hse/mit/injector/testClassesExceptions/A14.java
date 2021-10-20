package ru.hse.mit.injector.testClassesExceptions;

public class A14 extends AbstractClass2 {

    public A14(Interface4 x) {
        assert x instanceof A13;
    }

    @Override
    public String foo() {
        return "O";
    }

    @Override
    public String bar() {
        return "K";
    }
}