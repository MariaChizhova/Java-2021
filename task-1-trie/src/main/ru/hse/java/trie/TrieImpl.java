package ru.hse.java.trie;

import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TrieImpl implements Trie {
    private static class Node {
        private final HashMap<Character, Node> child = new HashMap<>();
        private boolean isEndOfWord;
        private int suffixNumber;

        Map<Character, Node> getChild() {
            return child;
        }

        void setEndOfWord(boolean isEndOfWord) {
            this.isEndOfWord = isEndOfWord;
        }
    }

    private final Node root = new Node();

    @Override
    public boolean add(@NotNull String element) {
        if (contains(element)) {
            return false;
        }
        Node cur = root;
        for (char l : element.toCharArray()) {
            cur.suffixNumber++;
            cur = cur.getChild().computeIfAbsent(l, c -> new Node());
        }
        cur.suffixNumber++;
        cur.setEndOfWord(true);
        return true;
    }

    @Override
    public boolean contains(@NotNull String element) {
        Node cur = root;
        for (Character c : element.toCharArray()) {
            Node node = cur.getChild().get(c);
            if (node == null) {
                return false;
            }
            cur = node;
        }
        return cur.isEndOfWord;
    }

    @Override
    public boolean remove(@NotNull String element) {
        if (!contains(element)) {
            return false;
        }
        Node cur = root;
        for (char c : element.toCharArray()) {
            Node child = cur.getChild().get(c);
            cur.suffixNumber--;
            if (child.suffixNumber == 1) {
                cur.getChild().remove(c);
                return true;
            }
            cur = child;
        }
        cur.suffixNumber--;
        cur.setEndOfWord(false);
        return true;
    }

    @Override
    public int size() {
        return root.suffixNumber;
    }

    @Override
    public int howManyStartsWithPrefix(@NotNull String prefix) {
        Node cur = root;
        for (Character l : prefix.toCharArray()) {
            if (cur.getChild().get(l) == null) {
                return 0;
            }
            cur = cur.getChild().get(l);
        }
        return cur.suffixNumber;
    }

    @Override
    @Nullable
    public String nextString(@NotNull String element, int k) {
        if (k < 0) {
            throw new IllegalArgumentException("k must be >= 0");
        }
        if (k == 0) {
            if (!contains(element))
                return null;
            return element;
        }
        StringBuilder res = new StringBuilder();
        Node cur = root;
        k += (cur.isEndOfWord ? 1 : 0);
        for (Character c : element.toCharArray()) {
            for (Map.Entry<Character, Node> p : cur.getChild().entrySet()) {
                if (p.getKey() < c) {
                    k += p.getValue().suffixNumber;
                }
            }
            if (!cur.getChild().containsKey(c))
                break;
            cur = cur.getChild().get(c);
            k += (cur.isEndOfWord ? 1 : 0);
        }
        if (size() < k)
            return null;
        cur = root;
        while (true) {
            k -= (cur.isEndOfWord ? 1 : 0);
            if (k == 0)
                return res.toString();
            for (Map.Entry<Character, Node> p : cur.getChild().entrySet()) {
                if (p.getValue().suffixNumber >= k) {
                    res.append(p.getKey());
                    cur = cur.getChild().get(p.getKey());
                    break;
                }
                k -= p.getValue().suffixNumber;
            }
        }
    }
}
