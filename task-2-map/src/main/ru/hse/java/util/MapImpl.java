package ru.hse.java.util;

import org.jetbrains.annotations.NotNull;

import java.util.*;

public class MapImpl<K, V> extends AbstractMap<K, V> implements Dictionary<K, V> {
    private int size;
    private int buckets = 2;
    private final double loadValue = 0.8;
    private final int resizeValue = 2;
    private ArrayList<List<Entry<K, V>>> array = new ArrayList<>(buckets);
    private Set<K> keySet;
    private Collection<V> valueCollection;
    private Set<Map.Entry<K, V>> entrySet;
    private static final int INITIAL_BUCKETS = 2;

    private static class Entry<K, V> implements Map.Entry<K, V> {
        private final K key;
        private V value;

        public Entry(K key, V value) {
            this.key = key;
            this.value = value;
        }

        public K getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }

        public V setValue(V newValue) {
            V oldValue = value;
            value = newValue;
            return oldValue;
        }
    }

    private void init(ArrayList<List<Entry<K, V>>> arr) {
        for (int i = 0; i < buckets; i++) {
            arr.add(new LinkedList<>());
        }
    }

    public MapImpl() {
        init(array);
    }

    private int getBucket(Object key) {
        return Math.abs(key.hashCode()) % buckets;
    }

    private boolean isFilledEnough() {
        return size >= Math.round(buckets * loadValue);
    }

    private boolean isFreeEnough() {
        return size <= Math.round(buckets * loadValue / resizeValue);
    }

    private void reHash() {
        ArrayList<List<Entry<K, V>>> newArray = new ArrayList<>(buckets);
        init(newArray);
        for (List<Entry<K, V>> lst : array) {
            for (Entry<K, V> entry : lst) {
                newArray.get(getBucket(entry.getKey())).add(entry);
            }
        }
        array = newArray;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean containsKey(@NotNull Object key) {
        return get(key) != null;
    }

    @Override
    public V get(@NotNull Object key) {
        int bucket = getBucket(key);
        List<Entry<K, V>> lst = array.get(bucket);
        for (Entry<K, V> entry : lst) {
            if (key.equals(entry.getKey())) {
                return entry.getValue();
            }
        }
        return null;
    }

    private void increaseCapacity() {
        buckets *= resizeValue;
    }

    private void decreaseCapacity() {
        buckets = (int) Math.round((double) buckets / (double) resizeValue);
    }

    @Override
    public V put(@NotNull K key, V value) {
        if (isFilledEnough()) {
            increaseCapacity();
            reHash();
        }
        int bucket = getBucket(key);
        List<Entry<K, V>> lst = array.get(bucket);
        for (Entry<K, V> entry : lst) {
            if (key.equals(entry.getKey())) {
                return entry.setValue(value);
            }
        }
        size++;
        array.get(bucket).add(new Entry<>(key, value));
        return null;
    }

    @Override
    public V remove(@NotNull Object key) {
        int bucket = getBucket(key);
        List<Entry<K, V>> lst = array.get(bucket);
        if (isFreeEnough()) {
            decreaseCapacity();
            reHash();
        }
        V value = get(key);
        for (Entry<K, V> entry : lst) {
            if (key.equals(entry.getKey())) {
                size--;
                lst.remove(entry);
                return value;
            }
        }
        return null;
    }

    @Override
    public void clear() {
        size = 0;
        buckets = INITIAL_BUCKETS;
        array.clear();
        init(array);
    }

    private class IteratorForSet implements Iterator<K> {
        private final Iterator<Entry<K, V>> delegateIterator;

        private IteratorForSet(Iterator<Entry<K, V>> delegateIterator) {
            this.delegateIterator = delegateIterator;
        }

        @Override
        public boolean hasNext() {
            return delegateIterator.hasNext();
        }

        @Override
        public K next() {
            return delegateIterator.next().getKey();
        }

        @Override
        public void remove() {
            delegateIterator.remove();
        }
    }

    private class IteratorForCollection implements Iterator<V> {
        private final Iterator<Entry<K, V>> delegateIterator;

        private IteratorForCollection(Iterator<Entry<K, V>> delegateIterator) {
            this.delegateIterator = delegateIterator;
        }

        @Override
        public boolean hasNext() {
            return delegateIterator.hasNext();
        }

        @Override
        public V next() {
            return delegateIterator.next().getValue();
        }

        @Override
        public void remove() {
            delegateIterator.remove();
        }
    }
    private class IteratorForEntrySet implements Iterator<Map.Entry<K, V>> {
        private final Iterator<Entry<K, V>> delegateIterator;

        private IteratorForEntrySet(Iterator<Entry<K, V>> delegateIterator) {
            this.delegateIterator = delegateIterator;
        }

        @Override
        public boolean hasNext() {
            return delegateIterator.hasNext();
        }

        @Override
        public Map.Entry<K, V> next() {
            return delegateIterator.next();
        }

        @Override
        public void remove() {
            delegateIterator.remove();
        }
    }
    public class MapIterator implements Iterator<Entry<K, V>> {
        private int index = 0;
        private Iterator<Entry<K, V>> elem;

        MapIterator() {
            elem = array.get(0).iterator();
            if (!elem.hasNext()) {
                nextIndex();
            }
        }

        private void nextIndex() {
            index++;
            while (index < array.size() && array.get(index).isEmpty()) {
                index++;
            }
            if (index != array.size()) {
                elem = array.get(index).iterator();
            }
        }

        @Override
        public boolean hasNext() {
            if (index == array.size()) {
                return false;
            }
            if (elem.hasNext()) {
                return true;
            }
            nextIndex();
            return elem.hasNext();
        }

        @Override
        public Entry<K, V> next() {
            if (index == array.size()) {
                throw new NoSuchElementException();
            }
            if (!elem.hasNext()) {
                nextIndex();
            }
            if (!elem.hasNext()) {
                throw new NoSuchElementException();
            }
            return elem.next();
        }

        @Override
        public void remove() {
            elem.remove();
            size--;
        }
    }

    @Override
    @NotNull
    public Set<K> keySet() {
        Set<K> newKeySet = keySet;
        if (newKeySet == null) {
            newKeySet = new AbstractSet<>() {
                @Override
                public @NotNull Iterator<K> iterator() {
                    return new IteratorForSet(new MapIterator());
                }

                @Override
                public int size() {
                    return size;
                }
            };
            keySet = newKeySet;
        }
        return newKeySet;
    }

    @Override
    @NotNull
    public Collection<V> values() {
        Collection<V> newValueCollection = valueCollection;
        if (newValueCollection == null) {
            newValueCollection = new AbstractCollection<>() {
                @Override
                public @NotNull Iterator<V> iterator() {
                    return new IteratorForCollection(new MapIterator());
                }

                @Override
                public int size() {
                    return size;
                }
            };
            valueCollection = newValueCollection;
        }
        return newValueCollection;
    }

    @Override
    public @NotNull Set<Map.Entry<K, V>> entrySet() {
        Set<Map.Entry<K, V>> newEntrySet = entrySet;
        if (newEntrySet == null) {
            newEntrySet = new AbstractSet<>() {
                @Override
                public @NotNull Iterator<Map.Entry<K, V>> iterator() {
                    return new IteratorForEntrySet(new MapIterator());
                }

                @Override
                public int size() {
                    return size;
                }
            };
            entrySet = newEntrySet;
        }
        return newEntrySet;
    }
}