package org.cthul.observe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public class ToObservable<R> implements Collector<Object, List<Object>, R> {
    
    public static <R> Function<Observable, ToObservable<R>> withType(Class<R> resultType) {
        return observable -> new ToObservable<>(observable, resultType);
    }
    
    private final Observable observable;
    private final Class<R> resultType;

    public ToObservable(Observable observable, Class<R> resultType) {
        this.observable = observable.copy();
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
}
