package org.cthul.observe;

import java.util.*;

public class TypedObserver implements Observer {

    private final Object observer;

    public TypedObserver(Object observer) {
        Objects.requireNonNull(observer, "observer");
        this.observer = observer;
    }

    @Override
    public <O, X extends Exception> void notify(Class<O> type, Event.Announcement<O, X> event) throws X {
        if (type.isInstance(observer)) {
            event.accept(type.cast(observer));
        }
    }

    @Override
    public <O, R, X extends Exception> R notify(Class<O> type, Event.Inquiry<O, R, X> event) throws X {
        if (type.isInstance(observer)) {
            return event.apply(type.cast(observer));
        }
        return null;
    }

    @Override
    public int hashCode() {
        return observer.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof TypedObserver to) {
            return observer.equals(to.observer);
        }
        return observer.equals(obj);
    }

    @Override
    public String toString() {
        return observer.toString();
    }
}
