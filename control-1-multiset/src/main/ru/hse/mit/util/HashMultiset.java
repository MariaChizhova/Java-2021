package ru.hse.mit.util;

import org.jetbrains.annotations.NotNull;

import java.util.*;

public class HashMultiset<E> extends AbstractCollection<E> implements Multiset<E> {
    private final LinkedHashMap<E, Integer> map = new LinkedHashMap<>();
    private int size;

    public int getCount(Object element) {
        return map.getOrDefault(element, 0);
    }

    @Override
    public boolean add(Object element) {
        size++;
        int cnt = getCount(element);
        map.put((E) element, cnt + 1);
        return true;
    }

    @Override
    public boolean remove(Object element) {
        int cnt = getCount(element);
        if (cnt == 0) {
            return false;
        }
        if (cnt == 1) {
            map.remove(element);
        } else {
            map.put((E) element, cnt - 1);
        }
        size--;
        return true;
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
    public void clear() {
        map.clear();
        size = 0;
    }

    @Override
    public boolean contains(Object o) {
        return map.containsKey(o);
    }

    @Override
    public int count(Object element) {
        return map.getOrDefault(element, 0);
    }

    @Override
    public Set<E> elementSet() {
        return map.keySet();
    }

    @Override
    public Set<Entry<E>> entrySet() {
        return new AbstractSet<Entry<E>>() {
            @Override
            public Iterator<Entry<E>> iterator() {
                return new Iterator<Entry<E>>() {
                    final Iterator<Map.Entry<E, Integer>> iterator = map.entrySet().iterator();
                    int bucketSize;

                    @Override
                    public boolean hasNext() {
                        return iterator.hasNext();
                    }

                    @Override
                    public Entry<E> next() {
                        if (!hasNext()) {
                            throw new NoSuchElementException();
                        }
                        Map.Entry<E, Integer> bucket = iterator.next();
                        bucketSize = bucket.getValue();
                        return new Entry<E>() {
                            @Override
                            public E getElement() {
                                return bucket.getKey();
                            }

                            @Override
                            public int getCount() {
                                return bucket.getValue();
                            }
                        };
                    }

                    @Override
                    public void remove() {
                        iterator.remove();
                        size -= bucketSize;
                    }
                };
            }

            @Override
            public int size() {
                return map.size();
            }
        };
    }

    @NotNull
    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {
            private Iterator<Map.Entry<E, Integer>> iterator = map.entrySet().iterator();
            private E currentElement;
            private int bucketSize;
            private boolean removed;

            @Override
            public boolean hasNext() {
                return bucketSize > 0 || iterator.hasNext();
            }

            @Override
            public E next() {
                if (!hasNext()) {
                    throw new NoSuchElementException("Iterator has been exhausted");
                }
                if (bucketSize == 0) {
                    Map.Entry<E, Integer> next = iterator.next();
                    currentElement = next.getKey();
                    bucketSize = next.getValue();
                }
                --bucketSize;
                removed = false;
                return currentElement;
            }

            public void remove() {
                if (removed) {
                    throw new IllegalStateException();
                }
                int cnt = getCount(currentElement);
                if (cnt == 1) {
                    iterator.remove();
                } else {
                    map.put(currentElement, cnt - 1);
                }
                size--;
                removed = true;
            }
        };
    }
}