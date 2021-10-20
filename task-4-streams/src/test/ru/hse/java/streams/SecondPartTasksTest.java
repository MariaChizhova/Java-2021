package ru.hse.java.streams;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.*;

import static ru.hse.java.streams.SecondPartTasks.*;

public class SecondPartTasksTest {
    @Test
    public void testFindQuotes() throws IOException {
        ArrayList<String> files = new ArrayList<>(Arrays.asList(
                "src/test/resources/file1.txt",
                "src/test/resources/file2.txt",
                "src/test/resources/file3.txt"
        ));
        ArrayList<String> expected = new ArrayList<>(Arrays.asList(
                "Two roads diverged in a yellow wood,",
                "To where it bent in the undergrowth.",
                "And having perhaps the better claim,",
                "Though as for that the passing there",
                "And both that morning equally lay",
                "Yet knowing how way leads on to way,",
                "I shall be telling this with a sigh",
                "Two roads diverged in a wood, and I—",
                "And there is a frown of disdain,",
                "Which you strive to forget in vain,",
                "For it sticks in the heart's deep core",
                "And it sticks in the deep backbone"
        ));
        Assertions.assertEquals(expected, SecondPartTasks.findQuotes(files, "in"));
    }

    @Test
    public void testPiDividedBy4() {
        Assertions.assertEquals(Math.PI / 4, piDividedBy4(), 0.01);
    }

    @Test
    public void testFindPrinter() {
        Map<String, List<String>> compositions = new HashMap<>();
        compositions.put("Oleg", new ArrayList<>(Arrays.asList("Trololo", "Cococo")));
        compositions.put("Mitya", new ArrayList<>(Arrays.asList("Ahahahah", "Aha", "Ahahaha")));
        compositions.put("Fedya", new ArrayList<>(Arrays.asList("Bla", "Blablabla", "Blabla", "Blablala")));
        Assertions.assertEquals("Fedya", SecondPartTasks.findPrinter(compositions));
    }
    @Test
    public void testFindPrinterEmpty() {
        Map<String, List<String>> compositions = new HashMap<>();
        Assertions.assertNull(SecondPartTasks.findPrinter(compositions));
    }
    @Test
    public void testCalculateGlobalOrder() {
        HashMap<String, Integer> samokat = new HashMap<>();
        samokat.put("water", 100);
        samokat.put("ice-cream", 50);
        samokat.put("chocolate", 15);

        HashMap<String, Integer> yandexLavka = new HashMap<>();
        yandexLavka.put("water", 20);
        yandexLavka.put("chocolate", 100);

        HashMap<String, Integer> noname = new HashMap<>();
        noname.put("ice-cream", 500);
        noname.put("chocolate", 105);

        ArrayList<Map<String, Integer>> goods = new ArrayList<>();
        goods.add(samokat);
        goods.add(yandexLavka);
        goods.add(noname);

        HashMap<String, Integer> actual = (HashMap<String, Integer>) SecondPartTasks.calculateGlobalOrder(goods);
        Assertions.assertEquals(Integer.valueOf(120), actual.get("water"));
        Assertions.assertEquals(Integer.valueOf(550), actual.get("ice-cream"));
        Assertions.assertEquals(Integer.valueOf(220), actual.get("chocolate"));
    }
}