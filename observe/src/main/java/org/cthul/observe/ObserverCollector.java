package org.cthul.observe;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public class ObserverCollector<R, O> implements Collector<O, ObserverCollector.Results<O>, R> {

    public static <O> Function<Subject, ObserverCollector<Subject, O>> toSubject() {
        return toSubject(Observer::cast);
    }

    public static <O> Function<Subject, ObserverCollector<Subject, O>> toSubject(Function<? super O, ? extends Observer> toObserver) {
        return subject -> new ObserverCollector<>(subject, toObserver, observeSubject(subject));
    }

    public static Collector<Void, Void, Void> noResult() {
        return NO_RESULT;
    }

    private static Function<Collection<Observer>, Subject> observeSubject(Subject subject) {
        return list -> {
            list.forEach(subject::addObserver);
            return subject;
        };
    }

    private final Subject subject;
    private final Function<? super O, ? extends Observer> toObserver;
    private final Function<? super Collection<Observer>, R> finisher;

    public ObserverCollector(Subject subject, Function<? super O, ? extends Observer> toObserver, Function<? super Collection<Observer>, R> finisher) {
        this.subject = subject;
        this.toObserver = toObserver;
        this.finisher = finisher;
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
        return results -> finisher.apply(results.bag);
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

    private static final Collector<Void, Void, Void> NO_RESULT = new Collector<>() {
        @Override
        public Supplier<Void> supplier() {
            return () -> null;
        }

        @Override
        public BiConsumer<Void, Void> accumulator() {
            return (a, b) -> {};
        }

        @Override
        public BinaryOperator<Void> combiner() {
            return (a, b) -> a;
        }

        @Override
        public Function<Void, Void> finisher() {
            return Function.identity();
        }

        @Override
        public Set<Collector.Characteristics> characteristics() {
            return EnumSet.allOf(Collector.Characteristics.class);
        }
    };
}
