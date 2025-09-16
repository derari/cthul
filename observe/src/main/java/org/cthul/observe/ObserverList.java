package org.cthul.observe;

import java.util.List;

public interface ObserverList extends Observable {

    List<Observer> getObserverList();

    @Override
    default Subscription addObserver(Observer observer) {
        getObserverList().add(observer);
        return new SubscriptionItem(this, observer);
    }

    default boolean removeObserver(Object observer) {
        return getObserverList().remove(Observer.from(observer));
    }

    record SubscriptionItem(ObserverList observable, Observer observer) implements Observable.Subscription {

        @Override
        public boolean unsubscribe() {
            return observable.removeObserver(observer);
        }
    }
}
