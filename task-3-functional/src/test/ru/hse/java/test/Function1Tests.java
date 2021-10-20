package ru.hse.java.test;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import ru.hse.java.Function1;

public class Function1Tests {
    private final Function1<Integer, Integer> addOne = x -> x + 1;
    private final Function1<Integer, Integer> multiplyByTwo = x -> x * 2;
    private final Function1<String, String> concat1 = s1 -> s1 + "1";
    private final Function1<String, String> concat2 = s1 -> s1 + "2";

    @Test
    public void testApply() {
        Assertions.assertEquals(Integer.valueOf(3), addOne.apply(2));
        Assertions.assertEquals(Integer.valueOf(4), multiplyByTwo.apply(2));
        Assertions.assertEquals("google1", concat1.apply("google"));
    }

    @Test
    public void testComposeDirectOrder() {
        Function1<Integer, Integer> addMultiplyCompose = addOne.compose(multiplyByTwo);
        Assertions.assertEquals(Integer.valueOf(6), addMultiplyCompose.apply(2));
        Function1<String, String> concatCompose = concat1.compose(concat2);
        Assertions.assertEquals("google12", concatCompose.apply("google"));
    }

    @Test
    public void testComposeInverseOrder() {
        Function1<Integer, Integer> multiplyAddCompose = multiplyByTwo.compose(addOne);
        Assertions.assertEquals(Integer.valueOf(5), multiplyAddCompose.apply(2));
        Function1<String, String> concatCompose = concat2.compose(concat1);
        Assertions.assertEquals("google21", concatCompose.apply("google"));
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

        Function1<Integer, R> f = R::new;
        Function1<ParentR, ChildV> g = x -> new ChildV(x.x);
        Assertions.assertEquals(Integer.valueOf(22), f.compose(g).apply(10).x);
    }
}
