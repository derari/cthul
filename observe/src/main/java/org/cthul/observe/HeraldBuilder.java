package org.cthul.observe;

import java.util.function.Function;
import java.util.stream.Collector;

public class HeraldBuilder implements Herald.Builder {
    
    private final Subject subject;
    private final AdaptiveBuilder<Herald> adaptive;

    public HeraldBuilder(Subject subject) {
        this.subject = subject;
        this.adaptive = new AdaptiveBuilder<>(this);
    }
    
    @SuppressWarnings("LeakingThisInConstructor")
    protected HeraldBuilder(Subject subject, AdaptiveBuilder<Herald> adaptive) {
        this.subject = subject;
        this.adaptive = adaptive.forInstance(this);
    }

    public HeraldBuilder forSubject(Subject newSubject) {
        return new HeraldBuilder(newSubject, adaptive);
    }
    
    public HeraldBuilder copy() {
        return new HeraldBuilder(subject, adaptive.copy());
    }
    
    public Herald build() {
        return copy();
    }

    @Override
    public <T> T as(Class<T> clazz, Function<? super Herald, ? extends T> ifUndeclared) {
        return adaptive.as(clazz, ifUndeclared);
    }

    @Override
    public <T> HeraldBuilder declare(Class<T> clazz, Function<? super Herald, ? extends T> intf) {
        adaptive.declare(clazz, intf);
        return this;
    }

    @Override
    @SafeVarargs
    public final HeraldBuilder declare(Function<? super Herald, ?>... intf) {
        Herald.Builder.super.declare(intf);
        return this;
    }

    @Override
    public <O, X extends Exception> void announce(Class<O> observer, Event.C0<O, X> event) throws X {
        for (var o: subject.getObserverList()) {
            o.notify(observer, event);
        }
    }

    @Override
    public <O, T, R, X extends Exception> R enquire(Class<O> observer, Function<? super Subject.Builder, ? extends Collector<? super T, ?, ? extends R>> collector, Event.F0<O, T, X> event) throws X {
        return collect(observer, collector.apply(subject.copy()), event);
    }

    @Override
    public <O, T, R, X extends Exception> R enquire(Class<O> observer, Collector<? super T, ?, ? extends R> collector, Event.F0<O, T, X> event) throws X {
        return collect(observer, collector, event);
    }

    private <O, T, B, R, X extends Exception> R collect(Class<O> observer, Collector<? super T, B, ? extends R> collector, Event.F0<O, T, X> event) throws X {
        var bag = collector.supplier().get();
        var accumulator = collector.accumulator();
        for (var o: subject.getObserverList()) {
            var t = o.notify(observer, event);
            accumulator.accept(bag, t);
        }
        return collector.finisher().apply(bag);
    }
}
