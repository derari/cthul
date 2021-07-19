package org.cthul.observe;

public interface Observer {

    default <O, X extends Exception> void accept(Class<O> type, Notifier.C0<O, X> signal) throws X {
        apply(type, o -> { signal.accept(o); return null; });
    }

    <O, R, X extends Exception> R apply(Class<O> type, Notifier.F0<O, R, X> signal) throws X;

    interface Builder {

        Builder include(Object... included);

        Builder exclude(Object... excluded);
    }
}
