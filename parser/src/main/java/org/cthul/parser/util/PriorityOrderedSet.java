package org.cthul.parser.util;

import java.util.Iterator;

public class PriorityOrderedSet<T extends PriorityOrderedSet.Link> {
    
    private final IntMap<PriorityList<T>> lists = new IntMap<>();
    private PriorityList<T> top = null;

    public PriorityOrderedSet() {
    }
    
    public void add(T item){
        PriorityList<T> pl = lists.get(item.getPriority());
        if (pl == null) {
            addNewList(item);
        } else {
            pl.add(item);
        }
    }

    private void addNewList(T item) {
        int prio = item.getPriority();
        PriorityList<T> pl;
        if (top == null) {
            pl = new PriorityList<>(item);
            lists.put(prio, pl);
            top = pl;
        } else {
            int k = lists.findLowestKey(prio);
            pl = lists.get(k);
            if (pl.priority != prio) {
                lists.put(prio, pl.newList(item));
            }
        }
    }
    
    public Iterable<T> getAll(int priority) {
        PriorityList<T> pl = lists.get(priority);
        if (pl == null) {
            return new PriorityList<>(priority, null);
        } else {
            return pl;
        }
    }
    
    /**
     * Returns all rules with prioMin <= priority < prioTop.
     */
    public Iterable<T> getAll(int prioMin, Integer prioTop) {
        return new PriorityList<>(prioMin, prioTop);
    }
    
    
    public static abstract class Link {
        Link next = null;
        protected abstract int getPriority();
    }
    
    /**
     * Linked list of items with a minimum precedence
     */
    private class PriorityList<T extends Link> implements Iterable<T> {
        
        private final int priority;
        private final Integer prioTop;
        private T first;
        private T last;

        /** Only as iterable, not to be cached. */
        private PriorityList(int priority, Integer prioTop) {
            this.priority = priority;
            this.prioTop = prioTop;
        }
        
        private PriorityList(T p) {
            first = p;
            last = p;
            priority = p.getPriority();
            prioTop = null;
        }
        
        private PriorityList() {
            first = null;
            last = null;
            priority = -1;
            prioTop = null;
        }
        
        private void add(T p) {
            p.next = last.next;
            last.next = p;
            last = p;
        }
        
        private PriorityList newList(T p) {
            p.next = last.next;
            last.next = p;
            return new PriorityList(p);
        }
        
        @Override
        public Iterator<T> iterator() {
            return new Itr<>(priority, prioTop, top.first);
        }

        @Override
        public String toString() {
            return Format.join("", ", ", "", this);
        }

    }
    
    private class Itr<T extends Link> implements Iterator<T> {
        
        private final int priority;
        private Link next;

        public Itr(int priority, Integer prioTop, Link first) {
            this.priority = priority;
            if (prioTop != null) {
                int max = prioTop;
                while (first != null && first.getPriority() >= prioTop) {
                    first = first.next;
                }
            }
            this.next = first;
        }

        @Override
        public boolean hasNext() {
            return next != null;
        }

        @Override
        public T next() {
            Link item = next;
            next = item.next;
            if (next != null && next.getPriority() < priority) next = null;
            return (T) item;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
        
    }
    
}
