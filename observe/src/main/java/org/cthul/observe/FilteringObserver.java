package org.cthul.observe;

import java.util.*;
import java.util.stream.Stream;

public class FilteringObserver implements Observer {

    private final Observer observer;
    private final Map<Class<?>, Boolean> observedTypes = new HashMap<>();
    private List<Class<?>> included;
    private List<Class<?>> excluded;

    public FilteringObserver(Object observer) {
        this(Observer.from(observer));
    }

    public FilteringObserver(Observer observer) {
        Objects.requireNonNull(observer, "observer");
        this.observer = observer;
    }
    
    protected FilteringObserver(FilteringObserver source) {
        this.observer = source.observer;
        this.observedTypes.putAll(source.observedTypes);
        if (source.included != null) included = new ArrayList<>(source.included);
        if (source.excluded != null) excluded = new ArrayList<>(source.excluded);
    }
    
    public FilteringObserver copy() {
        return new FilteringObserver(this);
    }
    
    public Observer build() {
        return copy();
    }

    public FilteringObserver include(Object... included) {
        Stream.of(included).forEach(this::addInclude);
        return this;
    }

    public FilteringObserver exclude(Object... excluded) {
        Stream.of(excluded).forEach(this::addExclude);
        return this;
    }

    private void addInclude(Object arg) {
        var clazz = getClass(arg, "include");
        if (included == null) included = new ArrayList<>();
        addTo(clazz, included);
    }

    private void addExclude(Object arg) {
        var clazz = getClass(arg, "exclude");
        if (excluded == null) excluded = new ArrayList<>();
        addTo(clazz, excluded);
    }
    
    private Class<?> getClass(Object arg, String name) {
        Objects.requireNonNull(arg, name);
        if (arg instanceof Class<?> clazz) {
            return clazz;
        }
        return arg.getClass();
    }
    
    private void addTo(Class<?> item, List<Class<?>> list) {
        if (list.stream().noneMatch(c -> c.isAssignableFrom(item))) {
            list.add(item);
            observedTypes.clear();
        }
    }

    @Override
    public <O, X extends Exception> void notify(Class<O> type, Event.Announcement<O, X> event) throws X {
        if (isObserving(type)) {
            observer.notify(type, event);
        }
    }

    @Override
    public <O, R, X extends Exception> R notify(Class<O> type, Event.Inquiry<O, R, X> event) throws X {
        if (isObserving(type)) {
            return observer.notify(type, event);
        }
        return null;
    }

    public boolean isObserving(Class<?> type) {
        return observedTypes.computeIfAbsent(type, this::testAgainstLists);
    }

    private boolean testAgainstLists(Class<?> type) {
        return isIncluded(type) && !isExcluded(type);
    }

    private boolean isIncluded(Class<?> type) {
        return included == null || included.stream().anyMatch(type::isAssignableFrom);
    }

    private boolean isExcluded(Class<?> type) {
        return excluded != null && excluded.stream().anyMatch(type::isAssignableFrom);
    }

    @Override
    public String toString() {
        return observer.toString() + "["
                + (included == null ? "" : "+" + included.size())
                + (excluded == null ? "" : "-" + excluded.size()) + "]";
    }

    public static class Builder extends FilteringObserver implements Observable.Subscription {

        private Observable.Subscription subscription;

        public Builder(Object observer) {
            super(observer);
        }

        public Builder(FilteringObserver source, Observable.Subscription subscription) {
            super(source);
            this.subscription = subscription;
        }

        public Builder withSubscription(Observable.Subscription subscription) {
            this.subscription = subscription;
            return this;
        }

        @Override
        public Observable observable() {
            return subscription == null ? null : subscription.observable();
        }

        @Override
        public Observer observer() {
            return this;
        }

        @Override
        public boolean unsubscribe() {
            return subscription != null && subscription.unsubscribe();
        }
    }
}
