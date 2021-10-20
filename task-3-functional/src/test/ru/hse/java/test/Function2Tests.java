package ru.hse.java.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.hse.java.Function1;
import ru.hse.java.Function2;

public class Function2Tests {
    private final Function1<Integer, Integer> addOne = x -> x + 1;
    private final Function2<Integer, Integer, Integer> addArgs = Integer::sum;
    private final Function2<Integer, Integer, Integer> multiplyArgs = (x, y) -> x * y;
    private final Function2<String, String, String> concat = (s1, s2) -> s1 + s2;

    @Test
    public void testApply() {
        Assertions.assertEquals(Integer.valueOf(5), addArgs.apply(2, 3));
        Assertions.assertEquals(Integer.valueOf(6), multiplyArgs.apply(2, 3));
    }

    @Test
    public void testCompose() {
        Function2<Integer, Integer, Integer> addOneAndArgsCompose = addArgs.compose(addOne);
        Assertions.assertEquals(Integer.valueOf(4), addOneAndArgsCompose.apply(1, 2));
        Function2<Integer, Integer, Integer> addMultiplyCompose = multiplyArgs.compose(addOne);
        Assertions.assertEquals(Integer.valueOf(7), addMultiplyCompose.apply(2, 3));
    }

    @Test
    public void testMostGeneralTypeCompose() {
        class ParentR {
            int x;
        }

        class V {
            int x;
        }

        class R extends ParentR {
            R(int x) {
                this.x = x * 2;
            }
        }

        class ChildV extends V {
            ChildV(int x) {
                this.x = x + 2;
            }
        }

        Function2<Integer, Integer, R> f = (x, y) -> new R(x + y);
        Function1<ParentR, ChildV> g = x -> new ChildV(x.x);
        Assertions.assertEquals(Integer.valueOf(12), f.compose(g).apply(2, 3).x);
    }

    @Test
    public void testBind1AddMultiply() {
        Function1<Integer, Integer> addTen = addArgs.bind1(10);
        Assertions.assertEquals(Integer.valueOf(25), addTen.apply(15));
        Function1<Integer, Integer> multiplyTen = multiplyArgs.bind1(10);
        Assertions.assertEquals(Integer.valueOf(50), multiplyTen.apply(5));
    }

    @Test
    public void testBind1Concat() {
        Function1<String, String> result = concat.bind1("h");
        Assertions.assertEquals("h", result.apply(""));
        Assertions.assertEquals("hello", result.apply("ello"));
    }

    @Test
    public void testBind2AddMultiply() {
        Function1<Integer, Integer> addTen = addArgs.bind2(10);
        Assertions.assertEquals(Integer.valueOf(25), addTen.apply(15));
        Function1<Integer, Integer> multiplyTen = multiplyArgs.bind1(10);
        Assertions.assertEquals(Integer.valueOf(50), multiplyTen.apply(5));
    }

    @Test
    public void testBind2Concat() {
        Function1<String, String> result = concat.bind2("o");
        Assertions.assertEquals("o", result.apply(""));
        Assertions.assertEquals("hello", result.apply("hell"));
    }

    @Test
    public void testCurry() {
        Function1<Integer, Function1<Integer, Integer>> addCurry = addArgs.curry();
        Assertions.assertEquals(Integer.valueOf(6), addCurry.apply(2).apply(4));
        Function1<Integer, Function1<Integer, Integer>> multiplyCurry = multiplyArgs.curry();
        Assertions.assertEquals(Integer.valueOf(6), multiplyCurry.apply(2).apply(3));
    }
}
