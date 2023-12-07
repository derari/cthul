package org.cthul.observe;

import java.util.stream.Stream;

public interface Observable {

    void addObserver(Observer observer);

    default void addObserver(Object observer) {
        addObserver(Observer.cast(observer));
    }

    default void addObservers(Object... observer) {
        Stream.of(observer).forEach(this::addObserver);
    }

    default void addObservers(Observer... observer) {
        Stream.of(observer).forEach(this::addObserver);
    }

    default FilteringObserver buildObserver(Object observer) {
        var builder = new FilteringObserver(observer);
        addObserver(builder);
        return builder;
    }
}
