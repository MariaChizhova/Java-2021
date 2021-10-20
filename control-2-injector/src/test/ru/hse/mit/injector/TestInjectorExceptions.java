package ru.hse.mit.injector;

import org.junit.jupiter.api.Test;
import ru.hse.mit.injector.testClassesExceptions.A12;
import ru.hse.mit.injector.testClassesExceptions.A6;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class TestInjectorExceptions {
    private static final String PACKAGE = "ru.hse.mit.injector.testClassesExceptions";

    @Test
    public void testImplNotFound() {
        assertThrows(ImplementationNotFoundException.class, () -> run("IrrelevantImpl"));
    }

    @Test
    public void testAmbiguousImpls1() {
        assertThrows(AmbiguousImplementationException.class, () -> run("A1", "A2", "A3", "A4", "A5"));
    }

    @Test
    public void testAmbiguousImpls2() {
        assertThrows(AmbiguousImplementationException.class, () -> run("A6", "A7", "A8"));
    }

    @Test
    public void testOnlyOneInstance() throws Exception {
        assertEquals("OK", ((A6) run("A6", "A7")).y.bar());
    }

    @Test
    public void testCyclic1() {
        assertThrows(InjectionCycleException.class, () -> run("A9", "A10"));
    }

    @Test
    public void testCyclic2() {
        assertThrows(InjectionCycleException.class, () -> run("A11"));
    }

    @Test
    public void testAbstractClass() throws Exception {
        assertTrue(run("A12", "A13", "A14") instanceof A12);
    }

    @Test
    public void testAbstractClassCycle() {
        assertThrows(InjectionCycleException.class, () -> run("A15", "A16"));
    }

    @Test
    public void testUnnecessaryImpl() throws Exception {
        run("A17", "A18");
    }

    private Object run(String... shortNames) throws Exception {
        ArrayList<String> fqNames = new ArrayList<>();
        for (String shortName : shortNames) {
            fqNames.add(PACKAGE + "." + shortName);
        }

        return Injector.initialize(fqNames.get(0), fqNames.subList(1, fqNames.size()));
    }
}