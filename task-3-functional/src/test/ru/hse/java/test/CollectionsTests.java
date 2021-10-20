package ru.hse.java.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.hse.java.Collections;
import ru.hse.java.Function1;
import ru.hse.java.Function2;
import ru.hse.java.Predicate;

import java.util.*;

public class CollectionsTests {
    private final Function1<Integer, Integer> multiplyByTwo = x -> x * 2;
    private final Function2<Integer, Integer, Integer> addArgs = Integer::sum;
    private final Function2<Integer, Integer, Integer> multiplyArgs = (x, y) -> x * y;
    private final Predicate<Integer> isEven = x -> (x % 2) == 0;
    private final Predicate<Integer> isOdd = x -> (x % 2) != 0;
    private final Predicate<Integer> isPositive = x -> (x > 0);
    private final Function2<Integer, Integer, Integer> divArgs = (x, y) -> x / y;
    private final Function1<String, String> concat1 = s1 -> s1 + "1";
    private final Predicate<String> isEvenLength = s1 -> (s1.length() % 2) == 0;
    private final Function2<String, String, String> concat = (s1, s2) -> s1 + s2;

    @Test
    public void testMapMultiply() {
        LinkedList<Integer> list = new LinkedList<>(Arrays.asList(2, 5, 1));
        LinkedList<Integer> result = (LinkedList<Integer>) Collections.map(multiplyByTwo, list);
        Assertions.assertEquals(new LinkedList<>(Arrays.asList(4, 10, 2)), result);
    }

    @Test
    public void testMapConcat() {
        LinkedList<String> list = new LinkedList<>(Arrays.asList("a", "b", "c"));
        LinkedList<String> result = (LinkedList<String>) Collections.map(concat1, list);
        Assertions.assertEquals(new LinkedList<>(Arrays.asList("a1", "b1", "c1")), result);
    }

    @Test
    public void testMostGeneralTypeMap() {
        class ParentX {
            int x;
        }

        class T extends ParentX { }

        class ChildT extends T {
            ChildT(int x) {
                this.x = x;
            }
        }

        Function1<ParentX, Integer> f = p -> p.x * 2;
        LinkedList<ChildT> list = new LinkedList<>(Arrays.asList(new ChildT(1), new ChildT(2)));
        Assertions.assertEquals( new LinkedList<>(Arrays.asList(2, 4)), Collections.map(f, list));
    }


    @Test
    public void testFilterIsEven() {
        LinkedList<Integer> list = new LinkedList<>(Arrays.asList(12, 15, 11, 14));
        LinkedList<Integer> result = (LinkedList<Integer>) Collections.filter(isEven, list);
        Assertions.assertEquals(new LinkedList<>(Arrays.asList(12, 14)), result);
    }

    @Test
    public void testFilterIsEvenLength() {
        LinkedList<String> list = new LinkedList<>(Arrays.asList("twitter", "google", "instagram", "facebook", "amazon"));
        LinkedList<String> result = (LinkedList<String>) Collections.filter(isEvenLength, list);
        Assertions.assertEquals(new LinkedList<>(Arrays.asList("google", "facebook", "amazon")), result);
    }

    @Test
    public void testMostGeneralTypeFilter() {
        class ParentX {
            int x;
        }

        class T extends ParentX {
        }

        class ChildT extends T {
            ChildT(int x) {
                this.x = x;
            }
        }

        Predicate<ParentX> f = p -> (p.x % 2) == 0;
        Function1<ParentX, Integer> g = p -> p.x;
        LinkedList<ChildT> list = new LinkedList<>(Arrays.asList(new ChildT(2), new ChildT(1), new ChildT(4)));
        Assertions.assertEquals(new LinkedList<>(Arrays.asList(2, 4)), Collections.map(g, Collections.filter(f, list)));
    }

    @Test
    public void testWhileWithIsOdd() {
        LinkedList<Integer> list = new LinkedList<>(Arrays.asList(11, 15, 10, 1, 34));
        LinkedList<Integer> result = (LinkedList<Integer>) Collections.takeWhile(isOdd, list);
        Assertions.assertEquals(new LinkedList<>(Arrays.asList(11, 15)), result);
    }

    @Test
    public void testWhileWithAlwaysTrue() {
        LinkedList<Integer> list = new LinkedList<>(Arrays.asList(11, 25, 10));
        LinkedList<Integer> result = (LinkedList<Integer>) Collections.takeWhile(Predicate.ALWAYS_TRUE, list);
        Assertions.assertEquals(new LinkedList<>(Arrays.asList(11, 25, 10)), result);
    }

    @Test
    public void testWhileIsEvenLength() {
        LinkedList<String> list = new LinkedList<>(Arrays.asList("google", "facebook", "instagram", "amazon", "twitter"));
        LinkedList<String> result = (LinkedList<String>) Collections.takeWhile(isEvenLength, list);
        Assertions.assertEquals(new LinkedList<>(Arrays.asList("google", "facebook")), result);
    }

    @Test
    public void testMostGeneralTypeWhile() {
        class ParentX {
            int x;
        }

        class T extends ParentX {
        }

        class ChildT extends T {
            ChildT(int x) {
                this.x = x;
            }
        }

        Predicate<ParentX> f = p -> (p.x % 2) == 0;
        Function1<ParentX, Integer> g = p -> p.x;
        LinkedList<ChildT> list = new LinkedList<>(Arrays.asList(new ChildT(2), new ChildT(4), new ChildT(3)));
        Assertions.assertEquals(new LinkedList<>(Arrays.asList(2, 4)), Collections.map(g, Collections.takeWhile(f, list)));
    }

    @Test
    public void testUnlessWithIsPositive() {
        LinkedList<Integer> list = new LinkedList<>(Arrays.asList(-11, -25, -10, 10, 24));
        LinkedList<Integer> result = (LinkedList<Integer>) Collections.takeUnless(isPositive, list);
        Assertions.assertEquals(new LinkedList<>(Arrays.asList(-11, -25, -10)), result);
    }

    @Test
    public void testUnlessWithAlwaysFalse() {
        LinkedList<Integer> list = new LinkedList<>(Arrays.asList(11, -5, 10));
        LinkedList<Integer> result = (LinkedList<Integer>) Collections.takeUnless(Predicate.ALWAYS_FALSE, list);
        Assertions.assertEquals(new LinkedList<>(), result);
    }

    @Test
    public void testUnlessIsEvenLength() {
        LinkedList<String> list = new LinkedList<>(Arrays.asList("instagram", "twitter", "google", "facebook", "amazon"));
        LinkedList<String> result = (LinkedList<String>) Collections.takeUnless(isEvenLength, list);
        Assertions.assertEquals(new LinkedList<>(Arrays.asList("instagram", "twitter")), result);
    }

    @Test
    public void testMostGeneralTypeUnless() {
        class ParentX {
            int x;
        }

        class T extends ParentX {
        }

        class ChildT extends T {
            ChildT(int x) {
                this.x = x;
            }
        }

        Predicate<ParentX> f = p -> (p.x % 2) == 0;
        Function1<ParentX, Integer> g = p -> p.x;
        LinkedList<ChildT> list = new LinkedList<>(Arrays.asList(new ChildT(3), new ChildT(2), new ChildT(3)));
        Assertions.assertEquals(new LinkedList<>(Arrays.asList(3)), Collections.map(g, Collections.takeUnless(f, list)));
    }

    @Test
    public void testFoldlAdd() {
        LinkedList<Integer> list = new LinkedList<>(Arrays.asList(11, 5, 1, 10, 2));
        int result = Collections.foldl(addArgs, 0, list);
        Assertions.assertEquals(29, result);
    }

    @Test
    public void testFoldlDiv() {
        LinkedList<Integer> list = new LinkedList<>(Arrays.asList(1, 2, 3, 4));
        int result = Collections.foldl(divArgs, 120, list);
        Assertions.assertEquals(5, result);
    }

    @Test
    public void testFoldlConcat() {
        LinkedList<String> list = new LinkedList<>(Arrays.asList("a", "b", "c", "d"));
        String result = Collections.foldl(concat, "", list);
        Assertions.assertEquals("abcd", result);
    }

    @Test
    public void testMostGeneralTypeFoldl() {
        class ParentX {
            int x;
        }

        class ParentY {
            int y;
        }

        class X extends ParentX { }

        class ChildX extends X {
            ChildX(int x) {
                this.x = x;
            }
        }

        class Y extends ParentY { }

        class ChildY extends Y {
            ChildY(int y) {
                this.y = y;
            }
        }

        Function2<ParentY, ParentX, ChildY> f = (y, x) -> new ChildY(x.x * y.y);
        LinkedList<ChildX> list = new LinkedList<>(Arrays.asList(new ChildX(5), new ChildX(1), new ChildX(2)));
        Y initialValue = new Y();
        initialValue.y = 1;
        Assertions.assertEquals(Integer.valueOf(10), Collections.foldl(f, initialValue, list).y);
    }

    @Test
    public void testFoldrMultiply() {
        LinkedList<Integer> list = new LinkedList<>(Arrays.asList(1, 2, 3));
        int result = Collections.foldr(multiplyArgs, 1, list);
        Assertions.assertEquals(6, result);
    }

    @Test
    public void testFoldrDiv() {
        LinkedList<Integer> list = new LinkedList<>(Arrays.asList(120, 30, 10, 2));
        int result = Collections.foldr(divArgs, 1, list);
        Assertions.assertEquals(20, result);
    }

    @Test
    public void testFoldrConcat() {
        LinkedList<String> list = new LinkedList<>(Arrays.asList("a", "b", "c", "d"));
        String result = Collections.foldr(concat, "", list);
        Assertions.assertEquals("abcd", result);
    }

    @Test
    public void testMostGeneralTypeFoldr() {
        class ParentX {
            int x;
        }

        class ParentY {
            int y;
        }

        class X extends ParentX { }

        class ChildX extends X {
            ChildX(int x) {
                this.x = x;
            }
        }

        class Y extends ParentY { }

        class ChildY extends Y {
            ChildY(int y) {
                this.y = y;
            }
        }

        Function2<ParentX, ParentY, ChildY> f = (x, y) -> new ChildY(x.x * y.y);
        LinkedList<ChildX> list = new LinkedList<>(Arrays.asList(new ChildX(3), new ChildX(2), new ChildX(4)));
        Y initialValue = new Y();
        initialValue.y = 1;
        Assertions.assertEquals(Integer.valueOf(24), Collections.foldr(f, initialValue, list).y);
    }
}
