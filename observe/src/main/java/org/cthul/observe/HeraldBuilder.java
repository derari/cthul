package org.cthul.observe;

import org.cthul.adapt.*;

import java.util.function.*;
import java.util.stream.Collector;

public class HeraldBuilder extends TypedAdaptiveBase<Herald, HeraldBuilder> implements Herald.Builder {
    
    private final Subject subject;

    public HeraldBuilder(Subject subject) {
        this.subject = subject;
    }
    
    protected HeraldBuilder(Subject subject, HeraldBuilder source) {
        super(source);
        this.subject = subject;
    }

    public HeraldBuilder copyForSubject(Subject newSubject) {
        return new HeraldBuilder(newSubject, this);
    }

    public HeraldBuilder copy() {
        return new HeraldBuilder(subject, this);
    }

    @Override
    public Herald build() {
        return copy();
    }

    @Override
    public <S, X extends Exception> void announce(Class<S> target, Event.C0<S, X> event) throws X {
        for (var o: subject.getObserverList()) {
            o.notify(target, event);
        }
    }

    @Override
    public <S, T, R, X extends Exception> R enquire(Class<S> target, Function<? super Subject.Builder, ? extends Collector<? super T, ?, ? extends R>> collector, Event.F0<S, T, X> event) throws X {
        return collect(target, collector.apply(subject.copy()), event);
    }

    @Override
    public <S, T, R, X extends Exception> R enquire(Class<S> target, Collector<? super T, ?, ? extends R> collector, Event.F0<S, T, X> event) throws X {
        return collect(target, collector, event);
    }

    private <T, R0, B, R, X extends Exception> R collect(Class<T> target, Collector<? super R0, B, ? extends R> collector, Event.F0<T, R0, X> event) throws X {
        var bag = collector.supplier().get();
        var accumulator = collector.accumulator();
        for (var o: subject.getObserverList()) {
            var t = o.notify(target, event);
            accumulator.accept(bag, t);
        }
        return collector.finisher().apply(bag);
    }

    @Override
    public String toString() {
        return "Herald@" + Integer.toHexString(System.identityHashCode(this)) + "(" + subject + ")";
    }
}
