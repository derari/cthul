package org.cthul.parser.util;

public class LinkedQueue<T extends LinkedQueue.Link> {

    private Link first;
    private Link last;
    
    public void enqueue(T link) {
        if (link.next != null || link == last) return;
        if (last == null) {
            first = last = link;
        } else {
            last.next = link;
            last = link;
        }
    }
    
    public T poll() {
        Link l = first;
        if (l != null) {
            Link next = l.next;
            l.next = null;
            first = next;
            if (next == null) last = null;
        }
        return (T) l;
    }
    
    public static class Link {
        Link next = null;
    }
    
}
