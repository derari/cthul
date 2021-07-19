package org.cthul.observe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collector;
import org.cthul.observe.Notifier.C0;
import org.cthul.observe.Notifier.F0;

import static org.cthul.observe.WrapperDefinitions.expandSynthetic;

public class BasicObservable implements Observable.Builder {

    private final WrapperDefinitions<Notifier> interfaces;
    private final Map<Class<?>, Object> notifiers = new HashMap<>();
    private final List<Observer> observers = new ArrayList<>();
    private final MyNotifier myNotifier = new MyNotifier();

    public BasicObservable() {
        this(new WrapperDefinitions<>());
    }

    public BasicObservable(WrapperDefinitions<Notifier> interfaceDefinitions) {
        this.interfaces = interfaceDefinitions;
    }

    public BasicObservable(WrapperDefinitions<Notifier> interfaceDefinitions, List<Observer> observers) {
        this(interfaceDefinitions);
        this.observers.addAll(observers);
    }

    @Override
    public <T> BasicObservable addInterface(Class<T> clazz, Function<? super Notifier, ? extends T> intf) {
        interfaces.add(clazz, intf);
        return this;
    }

    @Override
    public BasicObservable addInterface(Function<? super Notifier, ?> intf) {
        interfaces.add(intf);
        return this;
    }

    @Override
    public Notifier getNotifier() {
        return myNotifier;
    }

    @Override
    public <T> T getNotifier(Class<T> clazz) {
        if (clazz == null) throw new NullPointerException("class");
        if (clazz.isSynthetic()) throw new IllegalArgumentException(clazz + " is synthetic");
        Object result = notifiers.get(clazz);
        if (result != null) return (T) result;
        result = notifiers.values().stream()
                .filter(clazz::isInstance).findAny()
                .orElseGet(() -> wrapNotifierAs(clazz));
        notifiers.putIfAbsent(clazz, result);
        return (T) result;
    }

    private Object wrapNotifierAs(Class<?> clazz) {
        return interfaces.wrapAs(getNotifier(), clazz, this::addNotifier);
    }

    private void addNotifier(Object notifier) {
        expandSynthetic(notifier.getClass(), notifier, notifiers::putIfAbsent);
    }

    @Override
    public <T extends Observer> T addObserver(T observer) {
        if (observer == null) throw new NullPointerException("observer");
        observers.add(observer);
        return observer;
    }

    @Override
    public BasicObservable copy() {
        return new BasicObservable(interfaces, observers);
    }

    private class MyNotifier implements Notifier {

        @Override
        public <O, X extends Exception> void accept(Class<O> observer, C0<O, X> signal) throws X {
            for (Observer o: observers) {
                o.accept(observer, signal);
            }
        }

        @Override
        public <O, T, R, X extends Exception> R apply(Class<O> observer, Function<? super Observable.Builder, ? extends Collector<? super T, ?, ? extends R>> collector, F0<O, T, X> signal) throws X {
            return applyWith(observer, collector.apply(copy()), signal);
        }

        @Override
        public <O, T, R, X extends Exception> R apply(Class<O> observer, Collector<? super T, ?, ? extends R> collector, F0<O, T, X> signal) throws X {
            return applyWith(observer, collector, signal);
        }

        private <O, T, B, R, X extends Exception> R applyWith(Class<O> observer, Collector<? super T, B, ? extends R> collector, F0<O, T, X> signal) throws X {
            B bag = collector.supplier().get();
            BiConsumer<B, ? super T> accumulator = collector.accumulator();
            for (Observer o: observers) {
                T t = o.apply(observer, signal);
                accumulator.accept(bag, t);
            }
            return collector.finisher().apply(bag);
        }
    }
}
