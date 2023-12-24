package org.cthul.observe;

import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Stream;

@SuppressWarnings({"unused", "java:S107"})
public interface Herald extends Adaptive.Typed<Herald> {
    
    <S, T, R, X extends Exception> R enquire(Class<S> subject, Function<? super Subject.Builder, ? extends Collector<? super T, ?, ? extends R>> collector, Event.F0<S, T, X> event) throws X;

    default <S, X extends Exception> void announce(Class<S> subject, Event.C0<S, X> event) throws X {
        enquire(subject, ObserverCollector.noResult(), event.mapToNull());
    }

    default <S, R, X extends Exception> R enquire(Class<S> subject, Class<R> result, Event.F0<S, R, X> event) throws X {
        return enquire(subject, event).as(result);
    }

    default <S, X extends Exception> Subject enquire(Class<S> subject, Event.F0<S, ?, X> event) throws X {
        return enquire(subject, ObserverCollector.toSubject(), event);
    }

    default <S, T, R, X extends Exception> R enquire(Class<S> subject, Collector<? super T, ?, ? extends R> collector, Event.F0<S, T, X> event) throws X {
        return enquire(subject, s -> collector, event);
    }

    @Override
    default <T> T as(Class<T> clazz) {
        return as(clazz, HeraldInvocationProxy.castOrProxy(clazz));
    }

    @Override
    default <T> T as(Function<? super Herald, T> intf) {
        return intf.apply(this);
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
