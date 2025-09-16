package org.cthul.observe;

import java.util.*;
import java.util.function.*;
import java.util.stream.Collector;

public class ObserverCollector<O, R> implements Collector<O, ObserverCollector.Results<O>, R> {

    public static <S extends Subject, O> Function<S, ObserverCollector<O, S>> toSubject() {
        return ObserverCollector::addingTo;
    }

    public static <S extends Subject, O> Function<S, ObserverCollector<O, S>> toSubject(Function<? super O, ? extends Observer> toObserver) {
        return subject -> addingTo(subject, toObserver);
    }

    public static <S extends Subject, O> ObserverCollector<O, S> addingTo(S subject) {
        return addingTo(subject, Observer::from);
    }

    public static <S extends Subject, O> ObserverCollector<O, S> addingTo(S subject, Function<? super O, ? extends Observer> toObserver) {
        return new ObserverCollector<>(subject.getObserverList(), toObserver, addAllTo(subject));
    }

    public static Collector<Object, Void, Void> noResult() {
        return NO_RESULT;
    }

    private static <S extends Subject> Function<Iterable<? extends Observer>, S> addAllTo(S subject) {
        return list -> {
            subject.addObservers(list);
            return subject;
        };
    }

    private final Set<Observer> ignored;
    private final Function<? super O, ? extends Observer> toObserver;
    private final Function<? super Collection<Observer>, R> finisher;

    public ObserverCollector(Collection<? extends Observer> ignored, Function<? super O, ? extends Observer> toObserver, Function<? super Collection<Observer>, R> finisher) {
        this.ignored = new HashSet<>(ignored);
        this.toObserver = toObserver;
        this.finisher = finisher;
    }

    @Override
    public Supplier<Results<O>> supplier() {
        return () -> new Results<>(toObserver, ignored);
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

        private final Function<? super O, ? extends Observer> toObserver;
        private final Set<Observer> ignored;
        private final Set<Observer> bag = new LinkedHashSet<>();

        private Results(Function<? super O, ? extends Observer> toObserver, Set<Observer> ignored) {
            this.toObserver = toObserver;
            this.ignored = ignored;
        }

        private void add(O value) {
            if (value == null) return;
            var observer = toObserver.apply(value);
            if (ignored.contains(observer)) return;
            bag.add(observer);
        }

        private Results<O> addAll(Results<O> other) {
            bag.addAll(other.bag);
            return this;
        }
    }

    private static final Collector<Object, Void, Void> NO_RESULT = new Collector<>() {
        @Override
        public Supplier<Void> supplier() {
            return () -> null;
        }

        @Override
        public BiConsumer<Void, Object> accumulator() {
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
