package org.cthul.observe;

import java.util.stream.Stream;

public interface Observable {

    void addObserver(Observer observer);

    default void addObservers(Object... observer) {
        Stream.of(observer).forEach(this::addObserver);
    }

    default void addObservers(Observer... observer) {
        Stream.of(observer).forEach(this::addObserver);
    }

    default void addObserver(Object observer) {
        addObserver(Observer.cast(observer));
    }

    default ObserverBuilder buildObserver(Object observer) {
        var builder = new ObserverBuilder(observer);
        addObserver(builder);
        return builder;
    }
}
