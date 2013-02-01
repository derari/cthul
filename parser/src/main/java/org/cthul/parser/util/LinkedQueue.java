package org.cthul.parser.util;

public class LinkedQueue<T extends LinkedQueue.Link> {

    private T first;
    private T last;
    
    public void enqueue(T link) {
        if (link.next != null || link == last) return;
        if (last == null) {
            first = last = link;
        } else {
            last.next = link;
            last = link;
        }
    }
    
    public T peek() {
        return first;
    }
    
    @SuppressWarnings("unchecked")
    public T poll() {
        T l = first;
        if (l != null) {
            Link next = l.next;
            l.next = null;
            first = (T) next;
            if (next == null) last = null;
        }
        return l;
    }
    
    public static class Link {
        Link next = null;
    }
    
}
