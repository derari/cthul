package org.cthul.observe;

import java.util.List;

public interface ObserverList extends Observable {

    List<Observer> getObserverList();

    @Override
    default void addObserver(Observer observer) {
        getObserverList().add(observer);
    }

    default boolean removeObserver(Object observer) {
        return getObserverList().remove(Observer.cast(observer));
    }
}
