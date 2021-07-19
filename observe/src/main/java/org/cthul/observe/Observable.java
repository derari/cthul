package org.cthul.observe;

import java.util.function.Function;
import java.util.stream.Stream;

public interface Observable {

    static Builder build() {
        return new BasicObservable();
    }

    Notifier getNotifier();

    <T> T getNotifier(Class<T> clazz);

    default Observable addObservers(Object... observer) {
        Stream.of(observer).forEach(this::addObserver);
        return this;
    }

    default Observable addObservers(Observer... observer) {
        Stream.of(observer).forEach(this::addObserver);
        return this;
    }

    default Observer.Builder addObserver(Object observer) {
        return addObserver(new BasicObserver(observer));
    }

    <T extends Observer> T addObserver(T observer);

    Observable copy();

    interface Builder extends Observable {

        <T> Builder addInterface(Class<T> clazz, Function<? super Notifier, ? extends T> intf);

        Builder addInterface(Function<? super Notifier, ?> intf);
    }
}
