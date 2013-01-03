package org.cthul.parser.util;

import java.util.ArrayList;
import java.util.Iterator;

public abstract class PriorityList<E> implements Iterable<E> {

    private int lastPriority = Integer.MAX_VALUE;
    private final ArrayList<E> list = new ArrayList<>();
    
    public boolean add(E e, int p) {
        if (p > lastPriority) {
            throw new IllegalStateException(
                    "Maximum priority " + lastPriority + 
                    ", got " + p + " for " + e);
        }
        return list.add(e);
    }
    
    public int size() {
        return list.size();
    }
    
    public E get(int i) {
        return list.get(i);
    }

    @Override
    public Iterator<E> iterator() {
        return list.iterator();
    }
    
    public Iterable<E> getAll(int priority) {
        return new PriorityIterable(priority);
    }

    protected abstract int priorityOf(E e);
    
    private class PriorityIterable implements Iterable<E> {

        private final int priority;

        public PriorityIterable(int priority) {
            this.priority = priority;
        }

        @Override
        public Iterator<E> iterator() {
            return new PriorityIterator(priority, (Iterator) list.iterator());
        }
    }
    
    private class PriorityIterator implements Iterator<E> {
        
        private final Iterator<E> it;
        private final int priority;
        private E next;

        public PriorityIterator(int priority, Iterator<E> it) {
            this.it = it;
            this.priority = priority;
            getNext();
        }

        @Override
        public boolean hasNext() {
            return next != null;
        }

        @Override
        public E next() {
            E value = next;
            getNext();
            return value;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        private void getNext() {
            if (!it.hasNext()) {
                next = null;
                return;
            }
            next = it.next();
            if (priorityOf(next) < priority) {
                next = null;
            }
        }
        
    }
}
