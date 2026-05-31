package org.cthul.util;

import java.util.*;
import java.util.function.*;

public class CopyOnReadList<T> extends AbstractCollection<T> implements List<T> {

    private final List<T> data;
    private List<T> snapshot = null;
    private int modCount = 0;

    public CopyOnReadList() {
        this(new ArrayList<>());
    }

    public CopyOnReadList(List<T> data) {
        this.data = data;
    }

    List<T> write() {
        snapshot = null;
        modCount++;
        return data;
    }

    List<T> read() {
        var read = snapshot;
        if (read == null) {
            read = new ArrayList<>(data);
            snapshot = read;
        }
        return read;
    }

    @Override
    public int size() {
        return data.size();
    }

    @Override
    public T get(int index) {
        return data.get(index);
    }

    @Override
    public int indexOf(Object o) {
        return data.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return data.lastIndexOf(o);
    }

    @Override
    public boolean contains(Object o) {
        return data.contains(o);
    }

    @Override
    @SuppressWarnings("SlowListContainsAll")
    public boolean containsAll(Collection<?> c) {
        return data.containsAll(c);
    }

    @Override
    public boolean add(T t) {
        return write().add(t);
    }

    @Override
    public void add(int index, T element) {
        write().add(index, element);
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        return write().addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        return write().addAll(index, c);
    }

    @Override
    public boolean remove(Object o) {
        return write().remove(o);
    }

    @Override
    public T remove(int index) {
        return write().remove(index);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return write().removeAll(c);
    }

    @Override
    public T set(int index, T element) {
        return write().set(index, element);
    }

    @Override
    public void replaceAll(UnaryOperator<T> operator) {
        write().replaceAll(operator);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return write().retainAll(c);
    }

    @Override
    public void sort(Comparator<? super T> c) {
        write().sort(c);
    }

    @Override
    public boolean removeIf(Predicate<? super T> filter) {
        return write().removeIf(filter);
    }

    @Override
    public void clear() {
        write().clear();
    }

    @Override
    public Iterator<T> iterator() {
        return listIterator(0);
    }

    @Override
    public ListIterator<T> listIterator() {
        return listIterator(0);
    }

    @Override
    public ListIterator<T> listIterator(int index) {
        return new SnapshotIterator(index);
    }

    @Override
    public Spliterator<T> spliterator() {
        return read().spliterator();
    }

    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        return new SubList(data.subList(fromIndex, toIndex));
    }

    @Override
    public boolean equals(Object other) {
        return data.equals(other);
    }

    @Override
    public int hashCode() {
        return data.hashCode();
    }

    void verifyModCount(int expected) {
        if (modCount != expected) {
            throw new ConcurrentModificationException("concurrent modification");
        }
    }

    private class SnapshotIterator implements ListIterator<T> {

        List<T> snapshot = read();
        int expectedModCount = modCount;
        int index;
        int last = -1;

        public SnapshotIterator(int index) {
            this.index = index;
        }

        @Override
        public boolean hasNext() {
            return index < snapshot.size();
        }

        @Override
        public T next() {
            last = index;
            return snapshot.get(index++);
        }

        @Override
        public boolean hasPrevious() {
            return index > 0;
        }

        @Override
        public T previous() {
            last = --index;
            return snapshot.get(index);
        }

        @Override
        public int nextIndex() {
            return index + 1;
        }

        @Override
        public int previousIndex() {
            return index - 1;
        }

        @Override
        public void remove() {
            if (last < 0) throw new IllegalStateException();
            verifyModCount(expectedModCount);
            CopyOnReadList.this.remove(last);
            last = -1;
            postMod();
        }

        @Override
        public void set(T t) {
            if (last < 0) throw new IllegalStateException();
            verifyModCount(expectedModCount);
            CopyOnReadList.this.set(last, t);
            postMod();
        }

        @Override
        public void add(T t) {
            verifyModCount(expectedModCount);
            CopyOnReadList.this.add(index, t);
            last = -1;
            postMod();
        }

        private void postMod() {
            snapshot = read();
            expectedModCount = modCount;
        }
    }

    private class SubList extends CopyOnReadList<T> {

        int expectedModCount = CopyOnReadList.this.modCount;

        SubList(List<T> data) {
            super(data);
        }

        @Override
        List<T> write() {
            CopyOnReadList.this.write();
            expectedModCount = CopyOnReadList.this.modCount;
            return super.write();
        }

        @Override
        List<T> read() {
            if (expectedModCount != CopyOnReadList.this.modCount) {
                expectedModCount = CopyOnReadList.this.modCount;
                super.write();
            }
            return super.read();
        }

        @Override
        void verifyModCount(int expected) {
            CopyOnReadList.this.verifyModCount(expectedModCount);
            super.verifyModCount(expected);
        }
    }
}
