package org.cthul.observe;

import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public interface Observable {

    Subscription addObserver(Observer observer);

    default Subscription addObserver(Object observer) {
        return addObserver(Observer.from(observer));
    }

    default SubscriptionGroup addObservers(Object... observers) {
        return addObservers(List.of(observers));
    }

    default SubscriptionGroup addObservers(Observer... observers) {
        var list = Stream.of(observers).map(this::addObserver).toList();
        return new SubscriptionGroup(this, list);
    }

    default SubscriptionGroup addObservers(Iterable<?> observers) {
        var it = observers.spliterator();
        var list = StreamSupport.stream(it, false).
                map(Observer::from).
                map(this::addObserver)
                .toList();
        return new SubscriptionGroup(this, list);
    }

    default FilteringObserver.Builder buildObserver(Object observer) {
        var builder = Observer.filter(observer);
        var subscription = addObserver(builder);
        return builder.withSubscription(subscription);
    }

    interface Subscription {

        Observable observable();

        Observer observer();

        boolean unsubscribe();
    }

    record SubscriptionGroup(Observable observable, List<Subscription> all) {

        public boolean unsubscribeAll() {
            return all().stream().map(Subscription::unsubscribe).reduce(Boolean::logicalOr).orElse(false);
        }
    }
}
