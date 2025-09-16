package org.cthul.observe;

import org.cthul.adapt.Adaptive;

public interface Observer {
    
    static Observer from(Object observer) {
        if (observer instanceof Observer actual) {
            return actual;
        }
        if (observer instanceof Adaptive adaptive) {
            var adapter = adaptive.as(Observer.class);
            if (adapter != null) return adapter;
        }
        return new TypedObserver(observer);
    }

    static FilteringObserver.Builder filter(Object observer) {
        return new FilteringObserver.Builder(observer);
    }

    default <T, X extends Exception> void notify(Class<T> type, Event.Announcement<T, X> event) throws X {
        notify(type, event.asInquiry());
    }

    <T, R, X extends Exception> R notify(Class<T> type, Event.Inquiry<T, R, X> event) throws X;
}
