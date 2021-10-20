package ru.hse.java.test;

import ru.hse.java.trie.Trie;
import ru.hse.java.trie.TrieImpl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class TrieTests {
    private Trie trie;

    @BeforeEach
    public void init() {
        trie = new TrieImpl();
    }

    public void testedWordsWithDifferentRegisters() {
        trie.add("Google");
        trie.add("GoGoL");
        trie.add("gooogle");
        trie.add("gO");
    }

    public void testedWordsWithLowerCase() {
        trie.add("google");
        trie.add("gogol");
        trie.add("gooogle");
        trie.add("go");
    }

    @Test
    public void testAddOneStringTrue() {
        Assertions.assertTrue(trie.add("google"));
        Assertions.assertTrue(trie.contains("google"));
    }

    @Test
    public void testAddOneStringFalse() {
        Assertions.assertTrue(trie.add("google"));
        Assertions.assertFalse(trie.add("google"));
    }

    @Test
    public void testAddSomeStringTrue() {
        Assertions.assertTrue(trie.add("google"));
        Assertions.assertTrue(trie.add("gogol"));
        Assertions.assertTrue(trie.add("gooogle"));
        Assertions.assertTrue(trie.add("go"));
    }

    @Test
    public void testAddSomeStringFalse() {
        testedWordsWithLowerCase();
        Assertions.assertFalse(trie.add("go"));
        Assertions.assertFalse(trie.add("google"));
    }

    @Test
    public void testAddEmpty() {
        Assertions.assertTrue(trie.add(""));
        Assertions.assertTrue(trie.contains(""));
    }

    @Test
    public void testAddSomeStringWithDifferentRegistersTrue() {
        testedWordsWithDifferentRegisters();
        Assertions.assertTrue(trie.add("Go"));
        Assertions.assertTrue(trie.add("gooGle"));
    }

    @Test
    public void testAddSomeStringWithDifferentRegistersFalse() {
        testedWordsWithDifferentRegisters();
        Assertions.assertFalse(trie.add("gO"));
        Assertions.assertFalse(trie.add("Google"));
    }

    @Test
    public void testContainsTrue() {
        testedWordsWithLowerCase();
        Assertions.assertTrue(trie.contains("google"));
        Assertions.assertTrue(trie.contains("gogol"));
        Assertions.assertTrue(trie.contains("gooogle"));
        Assertions.assertTrue(trie.contains("go"));
    }

    @Test
    public void testContainsFalse() {
        testedWordsWithLowerCase();
        Assertions.assertFalse(trie.contains("gogle"));
        Assertions.assertFalse(trie.contains("ogol"));
        Assertions.assertFalse(trie.contains("oogle"));
        Assertions.assertFalse(trie.contains("o"));
    }

    @Test
    public void testContainsWithDifferentRegistersTrue() {
        trie.add("Google");
        trie.add("GoGoL");
        trie.add("goOOgle");
        trie.add("gO");
        Assertions.assertTrue(trie.contains("Google"));
        Assertions.assertTrue(trie.contains("GoGoL"));
        Assertions.assertTrue(trie.contains("goOOgle"));
        Assertions.assertTrue(trie.contains("gO"));
    }

    @Test
    public void testContainsWithDifferentRegistersFalse() {
        testedWordsWithDifferentRegisters();
        Assertions.assertFalse(trie.contains("google"));
        Assertions.assertFalse(trie.contains("Gogol"));
        Assertions.assertFalse(trie.contains("oogle"));
        Assertions.assertFalse(trie.contains("go"));
    }

    @Test
    public void testRemoveOneStringTrue() {
        testedWordsWithLowerCase();
        Assertions.assertTrue(trie.remove("go"));
    }

    @Test
    public void testRemoveOneStringFalse() {
        testedWordsWithLowerCase();
        Assertions.assertFalse(trie.remove("le"));
    }

    @Test
    public void testRemoveSomeStringsTrue() {
        testedWordsWithLowerCase();
        Assertions.assertTrue(trie.remove("go"));
        Assertions.assertTrue(trie.remove("gogol"));
    }

    @Test
    public void testRemoveSomeStringsFalse() {
        testedWordsWithLowerCase();
        Assertions.assertFalse(trie.remove("le"));
        Assertions.assertFalse(trie.remove("g"));
    }

    @Test
    public void testRemoveSomeStringsWithDifferentRegistersTrue() {
        trie.add("Google");
        trie.add("GoGoL");
        trie.add("goOOgle");
        trie.add("gO");
        Assertions.assertTrue(trie.remove("Google"));
        Assertions.assertTrue(trie.remove("GoGoL"));
        Assertions.assertTrue(trie.remove("goOOgle"));
        Assertions.assertTrue(trie.remove("gO"));
    }

    @Test
    public void testRemoveSomeStringsWithDifferentRegistersFalse() {
        testedWordsWithDifferentRegisters();
        Assertions.assertFalse(trie.remove("GooGle"));
        Assertions.assertFalse(trie.remove("GogoL"));
        Assertions.assertFalse(trie.remove("gooOgle"));
        Assertions.assertFalse(trie.remove("go"));
    }


    @Test
    public void testSizeEmptyTrie() {
        Assertions.assertEquals(0, trie.size());
    }

    @Test
    public void testSizeTrieWithEmptyString() {
        trie.add("");
        Assertions.assertEquals(1, trie.size());
    }

    @Test
    public void testSizeNonEmptyTrie() {
        trie.add("");
        testedWordsWithLowerCase();
        Assertions.assertEquals(5, trie.size());
    }

    @Test
    public void testSizeNonEmptyTrieWithEqualStrings() {
        testedWordsWithLowerCase();
        trie.add("gogol");
        trie.add("go");
        Assertions.assertEquals(4, trie.size());
    }

    @Test
    public void testSizeNonEmptyTrieAfterRemovingOneString() {
        trie.add("");
        testedWordsWithLowerCase();
        trie.remove("go");
        Assertions.assertEquals(4, trie.size());
    }

    @Test
    public void testSizeNonEmptyTrieAfterRemovingSomeString() {
        trie.add("google");
        trie.add("gogol");
        trie.add("gooogle");
        trie.add("gO");
        trie.remove("gO");
        trie.remove("google");
        Assertions.assertEquals(2, trie.size());
    }

    @Test
    public void testSizeNonEmptyTrieAfterRemovingEmptyString() {
        trie.add("");
        trie.remove("");
        Assertions.assertEquals(0, trie.size());
    }

    @Test
    public void testSizeNonEmptyTrieAfterRemovingAllString() {
        testedWordsWithLowerCase();
        trie.remove("google");
        trie.remove("gogol");
        trie.remove("gooogle");
        trie.remove("go");
        Assertions.assertEquals(0, trie.size());
    }

    @Test
    public void testHowManyStartsWithPrefixThatExists() {
        testedWordsWithLowerCase();
        trie.add("");
        trie.add("gog");
        trie.add("ogol");
        Assertions.assertEquals(5, trie.howManyStartsWithPrefix("g"));
        Assertions.assertEquals(1, trie.howManyStartsWithPrefix("ogol"));
        Assertions.assertEquals(7, trie.howManyStartsWithPrefix(""));
        Assertions.assertEquals(5, trie.howManyStartsWithPrefix("go"));
    }

    @Test
    public void testHowManyStartsWithPrefixThatDoesNotExists() {
        testedWordsWithLowerCase();
        trie.add("");
        trie.add("gog");
        trie.add("ogol");
        Assertions.assertEquals(2, trie.howManyStartsWithPrefix("gog"));
        Assertions.assertEquals(0, trie.howManyStartsWithPrefix("ggg"));
    }

    @Test
    public void testHowManyStartsWithPrefixWithDifferentRegisters() {
        trie.add("");
        trie.add("Google");
        trie.add("goGoL");
        trie.add("gOOOgle");
        trie.add("Go");
        trie.add("gOG");
        trie.add("Ogol");
        Assertions.assertEquals(3, trie.howManyStartsWithPrefix("g"));
        Assertions.assertEquals(0, trie.howManyStartsWithPrefix("ogol"));
        Assertions.assertEquals(7, trie.howManyStartsWithPrefix(""));
        Assertions.assertEquals(1, trie.howManyStartsWithPrefix("go"));
        Assertions.assertEquals(1, trie.howManyStartsWithPrefix("O"));
        Assertions.assertEquals(2, trie.howManyStartsWithPrefix("gO"));
    }

    @Test
    public void testNextStringWithNegativeK() {
        trie.add("google");
        trie.add("gogol");
        trie.add("gooogle");
        trie.add("go");
        trie.add("gog");
        trie.add("ogol");
        Assertions.assertThrows(IllegalArgumentException.class, () -> trie.nextString("google", -1));
    }

    @Test
    public void testNextStringWithKEqualsZeroSringExists() {
        testedWordsWithLowerCase();
        trie.add("gog");
        trie.add("ogol");
        Assertions.assertEquals("google", trie.nextString("google", 0));
    }

    @Test
    public void testNextStringWithKEqualsZeroStringIsMissing() {
        testedWordsWithLowerCase();
        trie.add("gog");
        trie.add("ogol");
        Assertions.assertNull(trie.nextString("ggle", 0));
    }

    @Test
    public void testNextStringWithPositiveKStringExists() {
        testedWordsWithLowerCase();
        trie.add("gog");
        trie.add("ogol");
        Assertions.assertEquals("gooogle", trie.nextString("google", 1));
        Assertions.assertEquals("gooogle", trie.nextString("gogol", 2));
        Assertions.assertEquals("ogol", trie.nextString("ogo", 1));
        Assertions.assertNull(trie.nextString("ogol", 2));
    }

    @Test
    public void testNextStringWithPositiveKStringIsMissing() {
        testedWordsWithLowerCase();
        trie.add("gog");
        trie.add("ogol");
        Assertions.assertNull(trie.nextString("ogl", 5));
        Assertions.assertNull(trie.nextString("snow", 1));
    }

    @Test
    public void testStress() {
        Set<String> correctTrie = new HashSet<>();
        int seed = 123;
        Random random = new Random(seed);
        for (int i = 0; i < 100_000; i++) {
            StringBuilder buf = new StringBuilder();
            int len = random.nextInt(10);
            for (int j = 0; j < len; j++) {
                buf.append('a' + random.nextInt(26));
            }
            int k = random.nextInt(4);
            if (k == 0) {
                Assertions.assertEquals(correctTrie.add(buf.toString()), trie.add(buf.toString()));
            } else if (k == 1) {
                Assertions.assertEquals(correctTrie.contains(buf.toString()), trie.contains(buf.toString()));
            } else if (k == 2) {
                Assertions.assertEquals(correctTrie.remove(buf.toString()), trie.remove(buf.toString()));
            } else {
                Assertions.assertEquals(correctTrie.size(), trie.size());
            }
        }
    }
}
