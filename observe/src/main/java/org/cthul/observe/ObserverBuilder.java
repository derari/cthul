package org.cthul.observe;

import java.util.*;
import java.util.stream.Stream;

public class ObserverBuilder implements Observer {

    private final Object observer;
    private final Map<Class<?>, Boolean> observedTypes = new HashMap<>();
    private List<Class<?>> included;
    private List<Class<?>> excluded;

    public ObserverBuilder(Object observer) {
        Objects.requireNonNull(observer, "observer");
        this.observer = observer;
    }
    
    protected ObserverBuilder(ObserverBuilder source) {
        this.observer = source.observer;
        this.observedTypes.putAll(source.observedTypes);
        if (source.included != null) included = new ArrayList<>(source.included);
        if (source.excluded != null) excluded = new ArrayList<>(source.excluded);
    }
    
    public ObserverBuilder copy() {
        return new ObserverBuilder(this);
    }
    
    public Observer build() {
        return copy();
    }

    public ObserverBuilder include(Object... included) {
        Stream.of(included).forEach(this::addInclude);
        return this;
    }

    public ObserverBuilder exclude(Object... excluded) {
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
        if (arg instanceof Class) {
            return (Class<?>) arg;
        }
        return arg.getClass();
    }
    
    private void addTo(Class<?> item, List<Class<?>> list) {
        if (list.stream().noneMatch(c -> c.isAssignableFrom(item))) {
            list.add(item);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <O, X extends Exception> void notify(Class<O> type, Event.C0<O, X> event) throws X {
        if (isObserving(type)) {
            event.accept((O) observer);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <O, R, X extends Exception> R notify(Class<O> type, Event.F0<O, R, X> event) throws X {
        if (isObserving(type)) {
            return event.apply((O) observer);
        }
        return null;
    }

    public boolean isObserving(Class<?> type) {
        return observedTypes.computeIfAbsent(type, this::checkObserving);
    }

    private boolean checkObserving(Class<?> type) {
        return isIncluded(type) && !isExcluded(type);
    }

    private boolean isIncluded(Class<?> type) {
        if (!type.isInstance(observer)) {
            return false;
        }
        return included == null || included.stream().anyMatch(type::isAssignableFrom);
    }

    private boolean isExcluded(Class<?> type) {
        return excluded != null && excluded.stream().anyMatch(type::isAssignableFrom);
    }

    @Override
    public int hashCode() {
        return observer.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ObserverBuilder) {
            return observer.equals(((ObserverBuilder) obj).observer);
        }
        return observer.equals(obj);
    }
}
