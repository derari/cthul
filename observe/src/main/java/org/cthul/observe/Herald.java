package org.cthul.observe;

import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Stream;

@SuppressWarnings({"unused", "java:S107"})
public interface Herald extends Adaptive.Typed<Herald> {
    
    default <S, X extends Exception> void announce(Class<S> subject, Event.C0<S, X> event) throws X {
        enquire(subject, ObserverCollector.noResult(), event.mapToNull());
    }

    default <S, R, X extends Exception> R enquire(Class<S> subject, Class<R> result, Event.F0<S, R, X> event) throws X {
        return enquire(subject, ObserverCollector.heraldAs(result), event);
    }

    <S, T, R, X extends Exception> R enquire(Class<S> subject, Function<? super Subject.Builder, ? extends Collector<? super T, ?, ? extends R>> collector, Event.F0<S, T, X> event) throws X;

    default <S, T, R, X extends Exception> R enquire(Class<S> subject, Collector<? super T, ?, ? extends R> collector, Event.F0<S, T, X> event) throws X {
        return enquire(subject, s -> collector, event);
    }

    default <S, A1, X extends Exception> void announce(Class<S> subject, Event.C1<S, A1, X> event, A1 arg1) throws X {
        announce(subject, event.curry(arg1));
    }

    default <S, A1, A2, X extends Exception> void announce(Class<S> subject, Event.C2<S, A1, A2, X> event, A1 arg1, A2 arg2) throws X {
        announce(subject, event.curry(arg1, arg2));
    }

    default <S, A1, A2, A3, X extends Exception> void announce(Class<S> subject, Event.C3<S, A1, A2, A3, X> event, A1 arg1, A2 arg2, A3 arg3) throws X {
        announce(subject, event.curry(arg1, arg2, arg3));
    }

    default <S, A1, A2, A3, A4, X extends Exception> void announce(Class<S> subject, Event.C4<S, A1, A2, A3, A4, X> event, A1 arg1, A2 arg2, A3 arg3, A4 arg4) throws X {
        announce(subject, event.curry(arg1, arg2, arg3, arg4));
    }

    default <S, A1, A2, A3, A4, A5, X extends Exception> void announce(Class<S> subject, Event.C5<S, A1, A2, A3, A4, A5, X> event, A1 arg1, A2 arg2, A3 arg3, A4 arg4, A5 arg5) throws X {
        announce(subject, event.curry(arg1, arg2, arg3, arg4, arg5));
    }

    default <S, R, A1, X extends Exception> R enquire(Class<S> subject, Class<R> result, Event.F1<S, A1, R, X> event, A1 arg1) throws X {
        return enquire(subject, result, event.curry(arg1));
    }

    default <S, R, A1, A2, X extends Exception> R enquire(Class<S> subject, Class<R> result, Event.F2<S, A1, A2, R, X> event, A1 arg1, A2 arg2) throws X {
        return enquire(subject, result, event.curry(arg1, arg2));
    }

    default <S, R, A1, A2, A3, X extends Exception> R enquire(Class<S> subject, Class<R> result, Event.F3<S, A1, A2, A3, R, X> event, A1 arg1, A2 arg2, A3 arg3) throws X {
        return enquire(subject, result, event.curry(arg1, arg2, arg3));
    }

    default <S, R, A1, A2, A3, A4, X extends Exception> R enquire(Class<S> subject, Class<R> result, Event.F4<S, A1, A2, A3, A4, R, X> event, A1 arg1, A2 arg2, A3 arg3, A4 arg4) throws X {
        return enquire(subject, result, event.curry(arg1, arg2, arg3, arg4));
    }

    default <S, R, A1, A2, A3, A4, A5, X extends Exception> R enquire(Class<S> subject, Class<R> result, Event.F5<S, A1, A2, A3, A4, A5, R, X> event, A1 arg1, A2 arg2, A3 arg3, A4 arg4, A5 arg5) throws X {
        return enquire(subject, result, event.curry(arg1, arg2, arg3, arg4, arg5));
    }

    default <S, T, R, A1, X extends Exception> R enquire(Class<S> subject, Function<? super Subject.Builder, ? extends Collector<? super T, ?, ? extends R>> collector, Event.F1<S, A1, T, X> event, A1 arg1) throws X {
        return enquire(subject, collector, event.curry(arg1));
    }

    default <S, T, R, A1, A2, X extends Exception> R enquire(Class<S> subject, Function<? super Subject.Builder, ? extends Collector<? super T, ?, ? extends R>> collector, Event.F2<S, A1, A2, T, X> event, A1 arg1, A2 arg2) throws X {
        return enquire(subject, collector, event.curry(arg1, arg2));
    }

    default <S, T, R, A1, A2, A3, X extends Exception> R enquire(Class<S> subject, Function<? super Subject.Builder, ? extends Collector<? super T, ?, ? extends R>> collector, Event.F3<S, A1, A2, A3, T, X> event, A1 arg1, A2 arg2, A3 arg3) throws X {
        return enquire(subject, collector, event.curry(arg1, arg2, arg3));
    }

    default <S, T, R, A1, A2, A3, A4, X extends Exception> R enquire(Class<S> subject, Function<? super Subject.Builder, ? extends Collector<? super T, ?, ? extends R>> collector, Event.F4<S, A1, A2, A3, A4, T, X> event, A1 arg1, A2 arg2, A3 arg3, A4 arg4) throws X {
        return enquire(subject, collector, event.curry(arg1, arg2, arg3, arg4));
    }

    default <S, T, R, A1, A2, A3, A4, A5, X extends Exception> R enquire(Class<S> subject, Function<? super Subject.Builder, ? extends Collector<? super T, ?, ? extends R>> collector, Event.F5<S, A1, A2, A3, A4, A5, T, X> event, A1 arg1, A2 arg2, A3 arg3, A4 arg4, A5 arg5) throws X {
        return enquire(subject, collector, event.curry(arg1, arg2, arg3, arg4, arg5));
    }

    default <S, T, R, A1, X extends Exception> R enquire(Class<S> subject, Collector<? super T, ?, ? extends R> collector, Event.F1<S, A1, T, X> event, A1 arg1) throws X {
        return enquire(subject, collector, event.curry(arg1));
    }

    default <S, T, R, A1, A2, X extends Exception> R enquire(Class<S> subject, Collector<? super T, ?, ? extends R> collector, Event.F2<S, A1, A2, T, X> event, A1 arg1, A2 arg2) throws X {
        return enquire(subject, collector, event.curry(arg1, arg2));
    }

    default <S, T, R, A1, A2, A3, X extends Exception> R enquire(Class<S> subject, Collector<? super T, ?, ? extends R> collector, Event.F3<S, A1, A2, A3, T, X> event, A1 arg1, A2 arg2, A3 arg3) throws X {
        return enquire(subject, collector, event.curry(arg1, arg2, arg3));
    }

    default <S, T, R, A1, A2, A3, A4, X extends Exception> R enquire(Class<S> subject, Collector<? super T, ?, ? extends R> collector, Event.F4<S, A1, A2, A3, A4, T, X> event, A1 arg1, A2 arg2, A3 arg3, A4 arg4) throws X {
        return enquire(subject, collector, event.curry(arg1, arg2, arg3, arg4));
    }

    default <S, T, R, A1, A2, A3, A4, A5, X extends Exception> R enquire(Class<S> subject, Collector<? super T, ?, ? extends R> collector, Event.F5<S, A1, A2, A3, A4, A5, T, X> event, A1 arg1, A2 arg2, A3 arg3, A4 arg4, A5 arg5) throws X {
        return enquire(subject, collector, event.curry(arg1, arg2, arg3, arg4, arg5));
    }
    
    interface Builder extends Herald, Adaptive.Builder<Herald, Herald.Builder> {

        @Override
        default <T> T as(Class<T> clazz) {
            if (clazz.isInterface()) {
                return as(clazz, HeraldInvocationProxy.factory(clazz));
            }
            return Adaptive.Builder.super.as(clazz);
        }

        default Herald.Builder declare(Class<?>... classes) {
            Stream.of(classes).forEach(this::declare);
            return this;
        }
        
        default <T> Herald.Builder declare(Class<T> clazz) {
            return declare(clazz, HeraldInvocationProxy.factory(clazz));
        }

        @Override
        default <T> T as(Function<? super Herald, T> intf) {
            return intf.apply(this);
        }
    }
}
