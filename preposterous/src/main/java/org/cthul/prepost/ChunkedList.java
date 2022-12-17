package org.cthul.prepost;

import java.util.*;

public class ChunkedList<T> extends AbstractList<T> {

    private final int chunkSize;
    private final List<List<T>> chunks = new ArrayList<>();
    private int size;

    public ChunkedList() {
        this(32);
    }

    public ChunkedList(int chunkSize) {
        this.chunkSize = chunkSize;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public T get(int index) {
        return null;
    }

    @Override
    public Iterator<T> iterator() {
        if (size == 0) return List.<T>of().iterator();
        var chunkIt = chunks.iterator();
        return new Iterator<T>() {
            Iterator<T> itemIt = chunkIt.next().iterator();
            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public T next() {
                return null;
            }
        };
    }
}
