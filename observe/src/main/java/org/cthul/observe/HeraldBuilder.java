package org.cthul.observe;

import org.cthul.adapt.TypedAdaptiveBase;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collector;

public class HeraldBuilder extends TypedAdaptiveBase<Herald, HeraldBuilder> implements Herald.Builder, Herald.EventAdapter {

    private final Subject subject;
    private BiFunction<Herald, Class<Object>, Object> defaultAdapter;

    public HeraldBuilder(Subject subject) {
        this.subject = subject;
        this.defaultAdapter = HeraldInvocationProxy.castOrProxy();
    }
    
    protected HeraldBuilder(HeraldBuilder source, Subject subject) {
        super(source);
        this.defaultAdapter = source.defaultAdapter;
        this.subject = subject;
    }

    public HeraldBuilder copyForSubject(Subject newSubject) {
        return new HeraldBuilder(this, newSubject);
    }

    public HeraldBuilder copy() {
        return new HeraldBuilder(this, subject);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public <T> BiFunction<Herald, Class<T>, T> defaultAdapter() {
        return (BiFunction) defaultAdapter;
    }

    public void setDefaultAdapter(BiFunction<Herald, Class<Object>, Object> defaultAdapter) {
        this.defaultAdapter = defaultAdapter;
    }

    @Override
    public Herald herald() {
        return this;
    }

    @Override
    public Herald build() {
        return copy();
    }

    @Override
    public <T, X extends Exception> void announce(Class<T> target, Event.Announcement<T, X> event) throws X {
        for (var o: subject.getObserverList()) {
            o.notify(target, event);
        }
    }

    @Override
    public <T, R0, R, X extends Exception> R inquire(Class<T> target, Function<? super Subject.Builder, ? extends Collector<? super R0, ?, ? extends R>> collectorBuilder, Event.Inquiry<T, R0, X> event) throws X {
        return collect(target, collectorBuilder.apply(subject.copy()), event);
    }

    @Override
    public <T, R0, R, X extends Exception> R inquire(Class<T> target, Collector<? super R0, ?, ? extends R> collector, Event.Inquiry<T, R0, X> event) throws X {
        return collect(target, collector, event);
    }

    private <T, R0, B, R, X extends Exception> R collect(Class<T> target, Collector<? super R0, B, ? extends R> collector, Event.Inquiry<T, R0, X> event) throws X {
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
        return "Herald@" + Integer.toHexString(hashCode()) + "(" + subject + ")";
    }
}
