package org.cthul.observe;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public class ObserverCollector<R, O> implements Collector<O, ObserverCollector.Results<O>, R> {

    public static <R> Function<Subject, ObserverCollector<R, Object>> heraldAs(Class<R> resultType) {
        return heraldAs(resultType, Observer::cast);
    }

    public static <R, O> Function<Subject, ObserverCollector<R, O>> heraldAs(Class<R> resultType, Function<? super O, ? extends Observer> toObserver) {
        return subject -> new ObserverCollector<>(subject, resultType, toObserver);
    }

    public static Collector<Object, Object, Object> noResult() {
        return NO_RESULT;
    }

    private final Subject subject;
    private final Class<R> resultType;
    private final Function<? super O, ? extends Observer> toObserver;

    public ObserverCollector(Subject subject, Class<R> resultType, Function<? super O, ? extends Observer> toObserver) {
        this.subject = subject;
        this.resultType = resultType;
        this.toObserver = toObserver;
    }

    @Override
    public Supplier<Results<O>> supplier() {
        return () -> new Results<>(toObserver, subject.getObserverList());
    }

    @Override
    public BiConsumer<Results<O>, O> accumulator() {
        return Results::add;
    }

    @Override
    public BinaryOperator<Results<O>> combiner() {
        return Results::addAll;
    }

    @Override
    public Function<Results<O>, R> finisher() {
        return results -> {
            results.bag.forEach(subject::addObserver);
            return subject.getHerald().as(resultType);
        };
    }

    @Override
    public Set<Characteristics> characteristics() {
        return Collections.emptySet();
    }

    public static class Results<O> {
        private final Set<Observer> existing;
        private final Set<Observer> bag = new LinkedHashSet<>();
        private final Function<? super O, ? extends Observer> toObserver;

        private Results(Function<? super O, ? extends Observer> toObserver, Collection<Observer> existing) {
            this.toObserver = toObserver;
            this.existing = new HashSet<>(existing);
        }

        private void add(O value) {
            if (value == null) return;
            add(toObserver.apply(value));
        }
        private void add(Observer observer) {
            if (existing.contains(observer)) return;
            bag.add(observer);
        }

        private Results<O> addAll(Results<O> other) {
            bag.addAll(other.bag);
            return this;
        }
    }

    private static final Collector<Object, Object, Object> NO_RESULT = new Collector<>() {
        @Override
        public Supplier<Object> supplier() {
            return () -> null;
        }

        @Override
        public BiConsumer<Object, Object> accumulator() {
            return (a, b) -> {};
        }

        @Override
        public BinaryOperator<Object> combiner() {
            return (a, b) -> a;
        }

        @Override
        public Function<Object, Object> finisher() {
            return Function.identity();
        }

        @Override
        public Set<Collector.Characteristics> characteristics() {
            return EnumSet.allOf(Collector.Characteristics.class);
        }
    };
}
