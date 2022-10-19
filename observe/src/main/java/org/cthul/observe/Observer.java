package org.cthul.observe;

public interface Observer {
    
    static Observer cast(Object observer) {
        if (observer instanceof Observer actual) return actual;
        return wrap(observer);
    }
    static ObserverBuilder wrap(Object observer) {
        return new ObserverBuilder(observer);
    }

    default <S, X extends Exception> void notify(Class<S> type, Event.C0<S, X> event) throws X {
        notify(type, event.mapToNull());
    }

    <S, R, X extends Exception> R notify(Class<S> type, Event.F0<S, R, X> event) throws X;
}
