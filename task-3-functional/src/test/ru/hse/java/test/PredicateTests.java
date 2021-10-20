package ru.hse.java.test;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.hse.java.Predicate;

public class PredicateTests {
    private final Predicate<Integer> isEven = x -> (x % 2) == 0;
    private final Predicate<Integer> isOdd = x -> (x % 2) != 0;
    private final Predicate<Integer> isPositive = x -> (x > 0);
    private final Predicate<String> isEvenLength = s1 -> (s1.length() % 2) == 0;
    private final Predicate<String> isOddLength = s1 -> (s1.length() % 2) != 0;
    private final Predicate<String> isEqualToHello = s1 -> s1.equals("Hello");
    private final Predicate<Number> isEqualTo100 = x -> x.equals(100);

    @Test
    public void testApply() {
        Assertions.assertTrue(isEven.apply(10));
        Assertions.assertTrue(isEven.apply(12));
        Assertions.assertFalse(isOdd.apply(10));
        Assertions.assertFalse(isOdd.apply(12));

        Assertions.assertTrue(isOdd.apply(11));
        Assertions.assertTrue(isOdd.apply(13));
        Assertions.assertFalse(isEven.apply(11));
        Assertions.assertFalse(isEven.apply(13));

        Assertions.assertTrue(isEvenLength.apply("abcd"));
        Assertions.assertFalse(isEvenLength.apply("abcde"));
        Assertions.assertTrue(isOddLength.apply("abcde"));
        Assertions.assertFalse(isOddLength.apply("abcd"));
    }

    @Test
    public void testOr() {
        final Predicate<Integer> isEvenOrPositive = isEven.or(isPositive);
        Assertions.assertTrue(isEvenOrPositive.apply(-10));
        Assertions.assertTrue(isEvenOrPositive.apply(23));
        Assertions.assertFalse(isEvenOrPositive.apply(-33));
        Assertions.assertFalse(isEvenOrPositive.apply(-9));

        final @NotNull Predicate<String> isEvenLengthOrEqualsToHello = isEvenLength.or(isEqualToHello);
        Assertions.assertTrue(isEvenLengthOrEqualsToHello.apply("Hello"));
        Assertions.assertTrue(isEvenLengthOrEqualsToHello.apply("facebook"));
        Assertions.assertFalse(isEvenLengthOrEqualsToHello.apply("hello"));
        Assertions.assertFalse(isEvenLengthOrEqualsToHello.apply("twitter"));

        final Predicate<Integer> isEvenOrIsEqualTo100 = isEven.or(isEqualTo100);
        Assertions.assertTrue(isEvenOrIsEqualTo100.apply(100));
        Assertions.assertTrue(isEvenOrIsEqualTo100.apply(80));
        Assertions.assertFalse(isEvenOrIsEqualTo100.apply(111));
    }

    @Test
    public void testAnd() {
        final Predicate<Integer> isOddAndPositive = isOdd.and(isPositive);
        Assertions.assertTrue(isOddAndPositive.apply(11));
        Assertions.assertTrue(isOddAndPositive.apply(43));
        Assertions.assertFalse(isOddAndPositive.apply(-32));
        Assertions.assertFalse(isOddAndPositive.apply(8));

        final @NotNull Predicate<String> isOddLengthAndEqualsToHello = isOddLength.or(isEqualToHello);
        Assertions.assertTrue(isOddLengthAndEqualsToHello.apply("Hello"));
        Assertions.assertFalse(isOddLengthAndEqualsToHello.apply("facebook"));

        final Predicate<Integer> isEvenAndIsEqualTo100 = isEven.and(isEqualTo100);
        Assertions.assertTrue(isEvenAndIsEqualTo100.apply(100));
        Assertions.assertFalse(isEvenAndIsEqualTo100.apply(102));
    }

    @Test
    public void testNot() {
        final Predicate<Integer> isNot = isPositive.not();
        Assertions.assertTrue(isNot.apply(-110));
        Assertions.assertTrue(isNot.apply(-5));
        Assertions.assertFalse(isNot.apply(11));
        Assertions.assertFalse(isNot.apply(10));

        final Predicate<String> isNotEvenLength = isEvenLength.not();
        Assertions.assertTrue(isNotEvenLength.apply("twitter"));
        Assertions.assertFalse(isNotEvenLength.apply("facebook"));
    }
}
