package org.cthul.observe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class BasicObserver implements Observer, Observer.Builder {

    private final Object observer;
    private final Map<Class<?>, Boolean> observedTypes = new HashMap<>();
    private List<Class<?>> included;
    private List<Class<?>> excluded;

    public BasicObserver(Object observer) {
        if (observer == null) throw new NullPointerException("observer");
        this.observer = observer;
    }

    @Override
    public BasicObserver include(Object... included) {
        Stream.of(included).forEach(this::addInclude);
        return this;
    }

    @Override
    public BasicObserver exclude(Object... excluded) {
        Stream.of(excluded).forEach(this::addExclude);
        return this;
    }

    private void addInclude(Object arg) {
        Class<?> clazz;
        if (arg == null) {
            throw new NullPointerException("include");
        } else if (arg instanceof Class) {
            clazz = (Class<?>) arg;
        } else {
            clazz = arg.getClass();
        }
        if (!clazz.isInstance(observer)) {
            throw new IllegalArgumentException(observer + " can not observe " + arg);
        }
        if (included == null) {
            included = new ArrayList<>();
        }
        included.add(clazz);
    }

    private void addExclude(Object arg) {
        Class<?> clazz;
        if (arg == null) {
            throw new NullPointerException("exclude");
        } else if (arg instanceof Class) {
            clazz = (Class<?>) arg;
        } else {
            clazz = arg.getClass();
        }
        if (excluded == null) {
            excluded = new ArrayList<>();
        }
        excluded.add(clazz);
    }

    public <O, X extends Exception> void accept(Class<O> type, Notifier.C0<O, X> signal) throws X {
        if (isObserving(type)) {
            signal.accept((O) observer);
        }
    }

    public <O, R, X extends Exception> R apply(Class<O> type, Notifier.F0<O, R, X> signal) throws X {
        if (isObserving(type)) {
            R result = signal.apply((O) observer);
            return result != observer ? result : null;
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
        if (included == null || included.isEmpty()) {
            return type.isInstance(observer);
        }
        return included.stream().anyMatch(type::isAssignableFrom);
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
        if (obj instanceof BasicObserver) {
            return observer.equals(((BasicObserver) obj).observer);
        }
        return false;
    }

}
