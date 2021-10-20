package ru.hse.java;

import org.jetbrains.annotations.NotNull;

public interface Predicate<X> {
    Predicate<Object> ALWAYS_TRUE = x -> true;
    Predicate<Object> ALWAYS_FALSE = x -> true;

    Boolean apply(X x);

    @NotNull
    default Predicate<X> or(@NotNull Predicate<? super X> that) {
        return (X x) -> this.apply(x) || that.apply(x);
    }

    @NotNull
    default Predicate<X> and(@NotNull Predicate<? super X> that) {
        return (X x) -> this.apply(x) && that.apply(x);
    }

    @NotNull
    default Predicate<X> not() {
        return (X x) -> !this.apply(x);
    }
}
