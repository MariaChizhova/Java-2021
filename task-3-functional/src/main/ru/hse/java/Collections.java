package ru.hse.java;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.LinkedList;
import java.util.ListIterator;

public class Collections {

    private Collections() {
    }

    @NotNull
    public static <X, R> Collection<R> map(@NotNull Function1<? super X, ? extends R> function,
                                           @NotNull Iterable<? extends X> collection
    ) {
        LinkedList<R> result = new LinkedList<>();
        for (X x : collection) {
            result.add(function.apply(x));
        }
        return result;
    }

    @NotNull
    public static <X> Collection<X> filter(@NotNull Predicate<? super X> predicate,
                                           @NotNull Iterable<? extends X> collection
    ) {
        LinkedList<X> result = new LinkedList<>();
        for (X x : collection) {
            if (predicate.apply(x)) {
                result.add(x);
            }
        }
        return result;
    }

    @NotNull
    public static <X> Collection<X> takeWhile(@NotNull Predicate<? super X> predicate,
                                              @NotNull Iterable<? extends X> collection
    ) {
        LinkedList<X> result = new LinkedList<>();
        for (X x : collection) {
            if (predicate.apply(x)) {
                result.add(x);
            } else {
                return result;
            }
        }
        return result;
    }

    @NotNull
    public static <X> Collection<X> takeUnless(Predicate<? super X> predicate,
                                               Iterable<? extends X> collection
    ) {
        return takeWhile(predicate.not(), collection);
    }

    public static <X, Y> X foldl(@NotNull Function2<? super X, ? super Y, ? extends X> function,
                                 X initialValue,
                                 @NotNull Iterable<? extends Y> collection
    ) {
        X acc = initialValue;
        for (Y y : collection) {
            acc = function.apply(acc, y);
        }
        return acc;
    }

    public static <X, Y> Y foldr(@NotNull Function2<? super X, ? super Y, ? extends Y> function,
                                 Y initialValue,
                                 @NotNull Iterable<? extends X> collection
    ) {
        LinkedList<X> result = new LinkedList<>();
        collection.forEach(result::add);
        ListIterator<X> iter = result.listIterator(result.size());
        Y acc = initialValue;
        while (iter.hasPrevious()) {
            acc = function.apply(iter.previous(), acc);
        }
        return acc;
    }
}
