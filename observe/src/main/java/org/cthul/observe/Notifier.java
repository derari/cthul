package org.cthul.observe;

import java.util.function.Function;
import java.util.stream.Collector;

public interface Notifier {

    default <O, X extends Exception> void accept(Class<O> observer, C0<O, X> signal) throws X {
        apply(observer, ToObservable.noResult(), o -> { signal.accept(o); return null; });
    }

    default <O, R, X extends Exception> R apply(Class<O> observer, Class<R> result, F0<O, R, X> signal) throws X {
        return apply(observer, ToObservable.withType(result), signal);
    }

    <O, T, R, X extends Exception> R apply(Class<O> observer, Function<? super Observable.Builder, ? extends Collector<? super T, ?, ? extends R>> collector, F0<O, T, X> signal) throws X;

    default <O, T, R, X extends Exception> R apply(Class<O> observer, Collector<? super T, ?, ? extends R> collector, F0<O, T, X> signal) throws X {
        return apply(observer, o -> collector, signal);
    }

    default <O, A1, X extends Exception> void accept(Class<O> observer, C1<O, A1, X> signal, A1 arg1) throws X {
        accept(observer, o -> signal.accept(o, arg1));
    }

    default <O, A1, A2, X extends Exception> void accept(Class<O> observer, C2<O, A1, A2, X> signal, A1 arg1, A2 arg2) throws X {
        accept(observer, o -> signal.accept(o, arg1, arg2));
    }

    default <O, A1, A2, A3, X extends Exception> void accept(Class<O> observer, C3<O, A1, A2, A3, X> signal, A1 arg1, A2 arg2, A3 arg3) throws X {
        accept(observer, o -> signal.accept(o, arg1, arg2, arg3));
    }

    default <O, A1, A2, A3, A4, X extends Exception> void accept(Class<O> observer, C4<O, A1, A2, A3, A4, X> signal, A1 arg1, A2 arg2, A3 arg3, A4 arg4) throws X {
        accept(observer, o -> signal.accept(o, arg1, arg2, arg3, arg4));
    }

    default <O, A1, A2, A3, A4, A5, X extends Exception> void accept(Class<O> observer, C5<O, A1, A2, A3, A4, A5, X> signal, A1 arg1, A2 arg2, A3 arg3, A4 arg4, A5 arg5) throws X {
        accept(observer, o -> signal.accept(o, arg1, arg2, arg3, arg4, arg5));
    }

    default <O, R, A1, X extends Exception> R apply(Class<O> observer, Class<R> result, F1<O, A1, R, X> signal, A1 arg1) throws X {
        return apply(observer, result, o -> signal.apply(o, arg1));
    }

    default <O, R, A1, A2, X extends Exception> R apply(Class<O> observer, Class<R> result, F2<O, A1, A2, R, X> signal, A1 arg1, A2 arg2) throws X {
        return apply(observer, result, o -> signal.apply(o, arg1, arg2));
    }

    default <O, R, A1, A2, A3, X extends Exception> R apply(Class<O> observer, Class<R> result, F3<O, A1, A2, A3, R, X> signal, A1 arg1, A2 arg2, A3 arg3) throws X {
        return apply(observer, result, o -> signal.apply(o, arg1, arg2, arg3));
    }

    default <O, R, A1, A2, A3, A4, X extends Exception> R apply(Class<O> observer, Class<R> result, F4<O, A1, A2, A3, A4, R, X> signal, A1 arg1, A2 arg2, A3 arg3, A4 arg4) throws X {
        return apply(observer, result, o -> signal.apply(o, arg1, arg2, arg3, arg4));
    }

    default <O, R, A1, A2, A3, A4, A5, X extends Exception> R apply(Class<O> observer, Class<R> result, F5<O, A1, A2, A3, A4, A5, R, X> signal, A1 arg1, A2 arg2, A3 arg3, A4 arg4, A5 arg5) throws X {
        return apply(observer, result, o -> signal.apply(o, arg1, arg2, arg3, arg4, arg5));
    }

    default <O, T, R, A1, X extends Exception> R apply(Class<O> observer, Function<? super Observable.Builder, ? extends Collector<? super T, ?, ? extends R>> collector, F1<O, A1, T, X> signal, A1 arg1) throws X {
        return apply(observer, collector, o -> signal.apply(o, arg1));
    }

    default <O, T, R, A1, A2, X extends Exception> R apply(Class<O> observer, Function<? super Observable.Builder, ? extends Collector<? super T, ?, ? extends R>> collector, F2<O, A1, A2, T, X> signal, A1 arg1, A2 arg2) throws X {
        return apply(observer, collector, o -> signal.apply(o, arg1, arg2));
    }

    default <O, T, R, A1, A2, A3, X extends Exception> R apply(Class<O> observer, Function<? super Observable.Builder, ? extends Collector<? super T, ?, ? extends R>> collector, F3<O, A1, A2, A3, T, X> signal, A1 arg1, A2 arg2, A3 arg3) throws X {
        return apply(observer, collector, o -> signal.apply(o, arg1, arg2, arg3));
    }

    default <O, T, R, A1, A2, A3, A4, X extends Exception> R apply(Class<O> observer, Function<? super Observable.Builder, ? extends Collector<? super T, ?, ? extends R>> collector, F4<O, A1, A2, A3, A4, T, X> signal, A1 arg1, A2 arg2, A3 arg3, A4 arg4) throws X {
        return apply(observer, collector, o -> signal.apply(o, arg1, arg2, arg3, arg4));
    }

    default <O, T, R, A1, A2, A3, A4, A5, X extends Exception> R apply(Class<O> observer, Function<? super Observable.Builder, ? extends Collector<? super T, ?, ? extends R>> collector, F5<O, A1, A2, A3, A4, A5, T, X> signal, A1 arg1, A2 arg2, A3 arg3, A4 arg4, A5 arg5) throws X {
        return apply(observer, collector, o -> signal.apply(o, arg1, arg2, arg3, arg4, arg5));
    }

    default <O, T, R, A1, X extends Exception> R apply(Class<O> observer, Collector<? super T, ?, ? extends R> collector, F1<O, A1, T, X> signal, A1 arg1) throws X {
        return apply(observer, collector, o -> signal.apply(o, arg1));
    }

    default <O, T, R, A1, A2, X extends Exception> R apply(Class<O> observer, Collector<? super T, ?, ? extends R> collector, F2<O, A1, A2, T, X> signal, A1 arg1, A2 arg2) throws X {
        return apply(observer, collector, o -> signal.apply(o, arg1, arg2));
    }

    default <O, T, R, A1, A2, A3, X extends Exception> R apply(Class<O> observer, Collector<? super T, ?, ? extends R> collector, F3<O, A1, A2, A3, T, X> signal, A1 arg1, A2 arg2, A3 arg3) throws X {
        return apply(observer, collector, o -> signal.apply(o, arg1, arg2, arg3));
    }

    default <O, T, R, A1, A2, A3, A4, X extends Exception> R apply(Class<O> observer, Collector<? super T, ?, ? extends R> collector, F4<O, A1, A2, A3, A4, T, X> signal, A1 arg1, A2 arg2, A3 arg3, A4 arg4) throws X {
        return apply(observer, collector, o -> signal.apply(o, arg1, arg2, arg3, arg4));
    }

    default <O, T, R, A1, A2, A3, A4, A5, X extends Exception> R apply(Class<O> observer, Collector<? super T, ?, ? extends R> collector, F5<O, A1, A2, A3, A4, A5, T, X> signal, A1 arg1, A2 arg2, A3 arg3, A4 arg4, A5 arg5) throws X {
        return apply(observer, collector, o -> signal.apply(o, arg1, arg2, arg3, arg4, arg5));
    }

    interface Notifications {

        Notifier notifier();
    }

    interface C0<O, X extends Exception> {
        void accept(O o) throws X;
    }

    interface C1<O, A1, X extends Exception> {
        void accept(O o, A1 a1) throws X;
    }

    interface C2<O, A1, A2, X extends Exception> {
        void accept(O o, A1 a1, A2 a2) throws X;
    }

    interface C3<O, A1, A2, A3, X extends Exception> {
        void accept(O o, A1 a1, A2 a2, A3 a3) throws X;
    }

    interface C4<O, A1, A2, A3, A4, X extends Exception> {
        void accept(O o, A1 a1, A2 a2, A3 a3, A4 a4) throws X;
    }

    interface C5<O, A1, A2, A3, A4, A5, X extends Exception> {
        void accept(O o, A1 a1, A2 a2, A3 a3, A4 a4, A5 a5) throws X;
    }

    interface F0<O, R, X extends Exception> {
        R apply(O o) throws X;
    }

    interface F1<O, A1, R, X extends Exception> {
        R apply(O o, A1 a1) throws X;
    }

    interface F2<O, A1, A2, R, X extends Exception> {
        R apply(O o, A1 a1, A2 a2) throws X;
    }

    interface F3<O, A1, A2, A3, R, X extends Exception> {
        R apply(O o, A1 a1, A2 a2, A3 a3) throws X;
    }

    interface F4<O, A1, A2, A3, A4, R, X extends Exception> {
        R apply(O o, A1 a1, A2 a2, A3 a3, A4 a4) throws X;
    }

    interface F5<O, A1, A2, A3, A4, A5, R, X extends Exception> {
        R apply(O o, A1 a1, A2 a2, A3 a3, A4 a4, A5 a5) throws X;
    }
}
