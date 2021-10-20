package ru.hse.mit.injector;

public class InjectionCycleException extends Exception {

    public InjectionCycleException() {
    }

    public InjectionCycleException(String message) {
        super(message);
    }

    public InjectionCycleException(String message, Throwable cause) {
        super(message, cause);
    }

    public InjectionCycleException(Throwable cause) {
        super(cause);
    }

    public InjectionCycleException(String message, Throwable cause, boolean enableSuppression,
        boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
