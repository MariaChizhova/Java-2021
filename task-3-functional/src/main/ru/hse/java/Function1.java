package ru.hse.java;

import org.jetbrains.annotations.NotNull;

public interface Function1<X, R> {
    R apply(X x);

    @NotNull
    default <V> Function1<X, V> compose(@NotNull Function1<? super R, ? extends V> g) {
        return (X x) -> g.apply(this.apply(x));
    }
}