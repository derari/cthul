package org.cthul.observe;

import java.util.stream.Stream;

public interface Observable {

    void addObserver(Observer observer);

    default void addObserver(Object observer) {
        addObserver(Observer.cast(observer));
    }

    default void addObservers(Object... observers) {
        Stream.of(observers).forEach(this::addObserver);
    }

    default void addObservers(Observer... observers) {
        Stream.of(observers).forEach(this::addObserver);
    }

    default void addObservers(Iterable<?> observers) {
        observers.forEach(this::addObserver);
    }

    default FilteringObserver buildObserver(Object observer) {
        var builder = Observer.filter(observer);
        addObserver(builder);
        return builder;
    }
}
