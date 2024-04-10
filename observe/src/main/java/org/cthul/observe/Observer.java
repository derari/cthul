package org.cthul.observe;

import org.cthul.adapt.Adaptive;

public interface Observer {
    
    static Observer cast(Object observer) {
        if (observer instanceof Observer actual) return actual;
        if (observer instanceof Adaptive adaptive) {
            var adapter = adaptive.as(Observer.class);
            if (adapter != null) return adapter;
        }
        return new TypedObserver(observer);
    }

    static FilteringObserver filter(Object observer) {
        return new FilteringObserver(observer);
    }

    default <S, X extends Exception> void notify(Class<S> type, Event.Announcement<S, X> event) throws X {
        notify(type, event.mapToNull());
    }

    <S, R, X extends Exception> R notify(Class<S> type, Event.Inquiry<S, R, X> event) throws X;
}
