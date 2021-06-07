package org.cthul.observe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Stream;
import org.cthul.observe.Notifier.C0;
import org.cthul.observe.Notifier.F0;

public class Observable {
    
    private final Map<Class<?>, Function<? super Notifier, ?>> interfaces;
    private final Map<Class<?>, Object> notifiers = new HashMap<>();
    private final List<Observer> observers = new ArrayList<>();
    private final MyNotifier myNotifier = new MyNotifier();

    public Observable() {
        this(new HashMap<>());
    }

    protected Observable(Map<Class<?>, Function<? super Notifier, ?>> interfaceDefinitions) {
        this.interfaces = interfaceDefinitions;
    }

    protected Observable(Map<Class<?>, Function<? super Notifier, ?>> interfaceDefinitions, List<Observer> observers) {
        this(interfaceDefinitions);
        this.observers.addAll(observers);
    }
    
    public <T> Observable addInterface(Class<T> clazz, Function<? super Notifier, ? extends T> intf) {
        interfaces.putIfAbsent(clazz, intf);
        return this;
    }

    public Notifier getNotifier() {
        return myNotifier;
    }
    
    public <T> T getNotifier(Class<T> clazz) {
        return (T) notifiers.computeIfAbsent(clazz, this::findNotifier);
    }
    
    protected <T> T findNotifier(Class<T> clazz) {
        if (clazz == null) throw new NullPointerException("clazz");
        return (T) notifiers
                .entrySet().stream()
                .filter(e -> clazz.isAssignableFrom(e.getKey()))
                .findAny()
                .map(e -> e.getValue())
                .orElseGet(() -> newNotifier(clazz));
    }
    
    protected <T> T newNotifier(Class<T> clazz) {
        Function<? super Notifier, ?> def = interfaces.computeIfAbsent(clazz, this::findInterface);
        if (def == null) {
            throw new IllegalArgumentException(""+ clazz);
        }
        return (T) def.apply(getNotifier());
    }
    
    protected Function<? super Notifier, ?> findInterface(Class<?> clazz) {
        return interfaces
                .entrySet().stream()
                .filter(e -> clazz.isAssignableFrom(e.getKey()))
                .findAny()
                .map(e -> {
                    interfaces.put(clazz, e.getValue());
                    return e.getValue();
                })
                .orElse(null);
    }
    
    public ObserverBuilder addObserver(Object observer) {
        if (observer == null) throw new NullPointerException("observer");
        Observer newObserver = new Observer(observer);
        observers.add(newObserver);
        return newObserver;
    }
    
    public Observable copy() {
        return new Observable(interfaces, observers);
    }
    
    private class MyNotifier implements Notifier {

        @Override
        public <O, X extends Exception> void accept(Class<O> observer, C0<O, X> signal) throws X {
            for (Observer o: observers) {
                o.accept(observer, signal);
            }
        }

        @Override
        public <O, T, R, X extends Exception> R apply(Class<O> observer, Function<? super Observable, ? extends Collector<? super T, ?, ? extends R>> collector, F0<O, T, X> signal) throws X {
            return applyWith(observer, collector.apply(Observable.this), signal);
        }

        @Override
        public <O, T, R, X extends Exception> R apply(Class<O> observer, Collector<? super T, ?, ? extends R> collector, F0<O, T, X> signal) throws X {
            return applyWith(observer, collector, signal);
        }
        
        private <O, T, A, R, X extends Exception> R applyWith(Class<O> observer, Collector<? super T, A, ? extends R> collector, F0<O, T, X> signal) throws X {
            A bag = collector.supplier().get();
            BiConsumer<A, ? super T> accumulator = collector.accumulator();
            for (Observer o: observers) {
                T t = o.apply(observer, signal);
                accumulator.accept(bag, t);
            }
            return collector.finisher().apply(bag);
        }
    }
    
    protected static class Observer implements ObserverBuilder {
        
        private final Object observer;
        private final Map<Class<?>, Boolean> observedTypes = new HashMap<>();
        private List<Class<?>> included;
        private List<Class<?>> excluded;

        public Observer(Object observer) {
            this.observer = observer;
        }

        @Override
        public ObserverBuilder include(Object... included) {
            Stream.of(included).forEach(this::addInclude);
            return this;
        }

        @Override
        public ObserverBuilder exclude(Object... excluded) {
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
            if (included == null) included = new ArrayList<>();
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
            if (excluded == null) excluded = new ArrayList<>();
            excluded.add(clazz);
        }
        
        public <O, X extends Exception> void accept(Class<O> type, C0<O, X> signal) throws X {
            if (isObserving(type)) {
                signal.accept((O) observer);
            }
        }
        
        public <O, R, X extends Exception> R apply(Class<O> type, F0<O, R, X> signal) throws X {
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
            return included.stream()
                    .anyMatch(type::isAssignableFrom);
        }
        
        private boolean isExcluded(Class<?> type) {
            return excluded != null && excluded.stream()
                    .anyMatch(type::isAssignableFrom);
        }

        @Override
        public int hashCode() {
            return observer.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Observer) {
                return observer.equals(((Observer) obj).observer);
            }
            return false;
        }
    }
    
    public static interface ObserverBuilder {
        
        ObserverBuilder include(Object... included);
        
        ObserverBuilder exclude(Object... excluded);
    }
}
