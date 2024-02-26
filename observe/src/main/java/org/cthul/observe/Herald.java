package org.cthul.observe;

import org.cthul.adapt.Adaptive;

import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Stream;

@SuppressWarnings({"unused", "java:S107"})
public interface Herald extends Adaptive.Typed<Herald> {
    
    <T, R0, R, X extends Exception> R enquire(Class<T> target, Function<? super Subject.Builder, ? extends Collector<? super R0, ?, ? extends R>> collector, Event.F0<T, R0, X> event) throws X;

    default <T, X extends Exception> void announce(Class<T> target, Event.C0<T, X> event) throws X {
        enquire(target, ObserverCollector.noResult(), event.mapToNull());
    }

    default <T, R, X extends Exception> R enquire(Class<T> target, Class<R> result, Event.F0<T, R, X> event) throws X {
        return enquire(target, event).getHerald().as(result);
    }

    default <T, X extends Exception> Subject.Builder enquire(Class<T> target, Event.F0<T, ?, X> event) throws X {
        return enquire(target, ObserverCollector.toSubject(), event);
    }

    default <T, R0, R, X extends Exception> R enquire(Class<T> target, Collector<? super R0, ?, ? extends R> collector, Event.F0<T, R0, X> event) throws X {
        return enquire(target, s -> collector, event);
    }

    @Override
    default <T> T as(Class<T> clazz) {
        return as(clazz, HeraldInvocationProxy.castOrProxy());
    }

    @Override
    default <T> T as(Function<? super Herald, T> adapt) {
        return adapt.apply(this);
    }

    interface Builder extends Herald, Adaptive.Builder<Herald, Herald.Builder> {

        default Herald.Builder declare(Class<?>... classes) {
            Stream.of(classes).forEach(this::declare);
            return this;
        }

        default <T> Herald.Builder declare(Class<T> clazz) {
            return declare(clazz, HeraldInvocationProxy.castOrProxy(clazz));
        }
    }
}
