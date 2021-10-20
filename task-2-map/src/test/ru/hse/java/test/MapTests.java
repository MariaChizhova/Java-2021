package ru.hse.java.test;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import ru.hse.java.util.MapImpl;

import java.util.*;


public class MapTests {

    @Test
    public void testPutOneWithIntegerValuesTrue() {
        MapImpl<Integer, Integer> map = new MapImpl<>();
        Assertions.assertNull(map.put(2, 7));
    }

    @Test
    public void testPutOneWithStringValuesTrue() {
        MapImpl<Integer, String> map = new MapImpl<>();
        Assertions.assertNull(map.put(2, "google"));
    }

    @Test
    public void testPutSomeWithDifferentValues() {
        Map<Integer, Integer> map = new MapImpl<>();
        map.put(2, 15);
        map.put(2, 8);
        Assertions.assertEquals(8, map.put(2, 11));
    }

    @Test
    public void testPutSomeWithDifferentValuesWithStringValuesTrue() {
        MapImpl<Integer, String> map = new MapImpl<>();
        map.put(3, "google");
        Assertions.assertEquals("google", map.put(3, "twitter"));
    }

    @Test
    public void testContainsKeyTrue() {
        MapImpl<Integer, Integer> map = new MapImpl<>();
        map.put(2, 54);
        map.put(12, 78);
        map.put(1, 35);
        map.put(7, 76);
        Assertions.assertTrue(map.containsKey(2));
        Assertions.assertTrue(map.containsKey(12));
        Assertions.assertTrue(map.containsKey(1));
        Assertions.assertTrue(map.containsKey(7));
    }

    @Test
    public void testContainsKeyFalse() {
        MapImpl<Integer, Integer> map = new MapImpl<>();
        map.put(2, 54);
        map.put(12, 78);
        map.put(1, 35);
        map.put(7, 76);
        Assertions.assertFalse(map.containsKey(3));
        Assertions.assertFalse(map.containsKey(5));
        Assertions.assertFalse(map.containsKey(4));
        Assertions.assertFalse(map.containsKey(23));
    }

    @Test
    public void testRemoveTrue() {
        MapImpl<Integer, Integer> map = new MapImpl<>();
        map.put(1, 2);
        map.put(2, 3);
        map.put(3, 4);
        map.put(4, 5);
        Assertions.assertEquals(2, map.remove(1));
        Assertions.assertEquals(3, map.remove(2));
        Assertions.assertEquals(4, map.remove(3));
        Assertions.assertEquals(5, map.remove(4));
    }

    @Test
    public void testRemoveFalse() {
        MapImpl<Integer, Integer> map = new MapImpl<>();
        map.put(1, 2);
        map.put(2, 3);
        map.put(3, 4);
        map.put(4, 5);
        Assertions.assertNull(map.remove(5));
        Assertions.assertNull(map.remove(6));
        Assertions.assertNull(map.remove(7));
    }

    @Test
    public void testSize() {
        MapImpl<Integer, Integer> map = new MapImpl<>();
        map.put(4, 2);
        map.put(6, 3);
        map.put(7, 4);
        map.put(2, 5);
        map.put(4, 3);
        Assertions.assertEquals(4, map.size());
    }

    @Test
    public void testSizeWithNullValue() {
        MapImpl<Integer, Integer> map = new MapImpl<>();
        map.put(1, null);
        map.remove(1);
        Assertions.assertEquals(0, map.size());
    }

    @Test
    public void testClear() {
        MapImpl<Integer, String> map = new MapImpl<>();
        map.put(1, "google");
        map.put(6, "twitter");
        map.put(7, "facebook");
        map.clear();
        Assertions.assertTrue(map.isEmpty());
        Assertions.assertFalse(map.containsKey(1));
        Assertions.assertFalse(map.containsKey(6));
        Assertions.assertFalse(map.containsKey(7));
    }

    @Test
    public void testStress() {
        Map<Integer, String> correctMap = new HashMap<>();
        MapImpl<Integer, String> map = new MapImpl<>();
        int seed = 123;
        Random random = new Random(seed);
        for (int i = 0; i < 10_000; i++) {
            StringBuilder buf = new StringBuilder();
            int len = random.nextInt(10);
            for (int j = 0; j < len; j++) {
                buf.append('a' + random.nextInt(26));
            }
            int k = random.nextInt(4);
            int key = random.nextInt(10_000);
            if (k == 0) {
                Assertions.assertEquals(correctMap.put(key, buf.toString()), map.put(key, buf.toString()));
            } else if (k == 1) {
                Assertions.assertEquals(correctMap.containsKey(key), map.containsKey(key));
            } else if (k == 2) {
                Assertions.assertEquals(correctMap.remove(key), map.remove(key));
            } else {
                Assertions.assertEquals(correctMap.size(), map.size());
            }
        }
    }

    private MapImpl<String, Integer> generateMap(int size, int bound, Map<String, Integer> correctMap) {
        MapImpl<String, Integer> map = new MapImpl<>();
        int seed = 123;
        Random random = new Random(seed);
        for (int i = 0; i < size; i++) {
            int rand = random.nextInt(bound);
            correctMap.put(Integer.toString(rand), i);
            map.put(Integer.toString(rand), i);
        }
        return map;
    }

    @Test
    public void testKeySet() {
        LinkedHashMap<String, Integer> correctMap = new LinkedHashMap<>();
        MapImpl<String, Integer> map;
        map = generateMap(10_000, 200, correctMap);
        Assertions.assertEquals(correctMap.keySet(), map.keySet());
    }

    @Test
    public void testValueCollection() {
        LinkedHashMap<String, Integer> correctMap = new LinkedHashMap<>();
        MapImpl<String, Integer> map;
        map = generateMap(10_000, 200, correctMap);
        ArrayList<Integer> allValuesMap = new ArrayList<>(map.values());
        for (Integer v : allValuesMap) {
            Assertions.assertTrue(correctMap.containsValue(v));
        }
        ArrayList<Integer> allValuesCorrectMap = new ArrayList<>(correctMap.values());
        for (Integer v : allValuesCorrectMap) {
            Assertions.assertTrue(map.containsValue(v));
        }
    }

   @Test
    public void testEntrySet() {
        LinkedHashMap<String, Integer> correctMap = new LinkedHashMap<>();
        MapImpl<String, Integer> map;
        map = generateMap(10_000, 200, correctMap);
        Assertions.assertEquals(correctMap.entrySet(), map.entrySet());
    }
}