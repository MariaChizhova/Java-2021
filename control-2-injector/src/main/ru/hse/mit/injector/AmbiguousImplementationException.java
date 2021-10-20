package ru.hse.mit.injector;

public class AmbiguousImplementationException extends Exception {
    public AmbiguousImplementationException() {
    }

    public AmbiguousImplementationException(String message) {
        super(message);
    }

    public AmbiguousImplementationException(String message, Throwable cause) {
        super(message, cause);
    }

    public AmbiguousImplementationException(Throwable cause) {
        super(cause);
    }

    public AmbiguousImplementationException(String message, Throwable cause, boolean enableSuppression,
        boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
