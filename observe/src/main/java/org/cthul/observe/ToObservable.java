package org.cthul.observe;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import org.cthul.observe.Observable;

public class ToObservable<R> implements Collector<Object, List<Object>, R> {

    public static <R> Function<Observable, ToObservable<R>> withType(Class<R> resultType) {
        return observable -> new ToObservable<>(observable, resultType);
    }

    public static Collector<Object, Object, Object> noResult() {
        return NO_RESULT;
    }

    private final Observable observable;
    private final Class<R> resultType;

    public ToObservable(Observable observable, Class<R> resultType) {
        this.observable = observable;
        this.resultType = resultType;
    }

    @Override
    public Supplier<List<Object>> supplier() {
        return ArrayList::new;
    }

    @Override
    public BiConsumer<List<Object>, Object> accumulator() {
        return (list, a) -> { if (a != null) list.add(a); };
    }

    @Override
    public BinaryOperator<List<Object>> combiner() {
        return (a, b) -> { a.addAll(b); return a; };
    }

    @Override
    public Function<List<Object>, R> finisher() {
        return all -> {
            all.forEach(observable::addObserver);
            return observable.getNotifier(resultType);
        };
    }

    @Override
    public Set<Characteristics> characteristics() {
        return Collections.emptySet();
    }

    private static final Collector<Object, Object, Object> NO_RESULT = new Collector<Object, Object, Object>() {
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
