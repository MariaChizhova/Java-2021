package ru.hse.java;

import org.jetbrains.annotations.NotNull;

public interface Function2<X, Y, R> {
    R apply(X x, Y y);

    @NotNull
    default <V> Function2<X, Y, V> compose(@NotNull Function1<? super R, ? extends V> g) {
        return (X x, Y y) -> g.apply(this.apply(x, y));
    }

    @NotNull
    default Function1<Y, R> bind1(X x) {
        return (Y y) -> this.apply(x, y);
    }

    @NotNull
    default Function1<X, R> bind2(Y y) {
        return (X x) -> this.apply(x, y);
    }

    @NotNull
    default Function1<X, Function1<Y, R>> curry() {
        return (X x) -> (Y y) -> apply(x, y);
    }
}
