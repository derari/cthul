package org.cthul.observe;

public interface Event {

    static <T, X extends Exception> Announcement<T, X> c(Announcement<T, X> event) {
        return event;
    }
    
    static <T, A1, X extends Exception> Announcement<T, X> c(C1<T, A1, X> event, A1 a1) {
        return event.curry(a1);
    }
    
    static <T, A1, A2, X extends Exception> Announcement<T, X> c(C2<T, A1, A2, X> event, A1 a1, A2 a2) {
        return event.curry(a1, a2);
    }
    
    static <T, A1, A2, A3, X extends Exception> Announcement<T, X> c(C3<T, A1, A2, A3, X> event, A1 a1, A2 a2, A3 a3) {
        return event.curry(a1, a2, a3);
    }
    
    static <T, A1, A2, A3, A4, X extends Exception> Announcement<T, X> c(C4<T, A1, A2, A3, A4, X> event, A1 a1, A2 a2, A3 a3, A4 a4) {
        return event.curry(a1, a2, a3, a4);
    }
    
    static <T, A1, A2, A3, A4, A5, X extends Exception> Announcement<T, X> c(C5<T, A1, A2, A3, A4, A5, X> event, A1 a1, A2 a2, A3 a3, A4 a4, A5 a5) {
        return event.curry(a1, a2, a3, a4, a5);
    }
    
    static <T, R, X extends Exception> Inquiry<T, R, X> f(Inquiry<T, R, X> event) {
        return event;
    }
    
    static <T, A1, R, X extends Exception> Inquiry<T, R, X> f(F1<T, A1, R, X> event, A1 a1) {
        return event.curry(a1);
    }
    
    static <T, A1, A2, R, X extends Exception> Inquiry<T, R, X> f(F2<T, A1, A2, R, X> event, A1 a1, A2 a2) {
        return event.curry(a1, a2);
    }
    
    static <T, A1, A2, A3, R, X extends Exception> Inquiry<T, R, X> f(F3<T, A1, A2, A3, R, X> event, A1 a1, A2 a2, A3 a3) {
        return event.curry(a1, a2, a3);
    }
    
    static <T, A1, A2, A3, A4, R, X extends Exception> Inquiry<T, R, X> f(F4<T, A1, A2, A3, A4, R, X> event, A1 a1, A2 a2, A3 a3, A4 a4) {
        return event.curry(a1, a2, a3, a4);
    }
    
    static <T, A1, A2, A3, A4, A5, R, X extends Exception> Inquiry<T, R, X> f(F5<T, A1, A2, A3, A4, A5, R, X> event, A1 a1, A2 a2, A3 a3, A4 a4, A5 a5) {
        return event.curry(a1, a2, a3, a4, a5);
    }

    interface Definitions {

        Herald herald();
    }

    interface Announcement<T, X extends Exception> extends Event {

        void accept(T s) throws X;

        default Inquiry<T, Void, X> mapToNull() {
            return t -> {
                accept(t);
                return null;
            };
        }
    }

    interface C1<T, A1, X extends Exception> extends Event {

        void accept(T t, A1 a1) throws X;

        default Announcement<T, X> curry(A1 a1) {
            return t -> accept(t, a1);
        }
    }

    interface C2<T, A1, A2, X extends Exception> extends Event {

        void accept(T t, A1 a1, A2 a2) throws X;

        default Announcement<T, X> curry(A1 a1, A2 a2) {
            return t -> accept(t, a1, a2);
        }
    }

    interface C3<T, A1, A2, A3, X extends Exception> extends Event {

        void accept(T t, A1 a1, A2 a2, A3 a3) throws X;

        default Announcement<T, X> curry(A1 a1, A2 a2, A3 a3) {
            return t -> accept(t, a1, a2, a3);
        }
    }

    interface C4<T, A1, A2, A3, A4, X extends Exception> extends Event {

        void accept(T t, A1 a1, A2 a2, A3 a3, A4 a4) throws X;

        default Announcement<T, X> curry(A1 a1, A2 a2, A3 a3, A4 a4) {
            return t -> accept(t, a1, a2, a3, a4);
        }
    }

    interface C5<T, A1, A2, A3, A4, A5, X extends Exception> extends Event {

        void accept(T t, A1 a1, A2 a2, A3 a3, A4 a4, A5 a5) throws X;

        default Announcement<T, X> curry(A1 a1, A2 a2, A3 a3, A4 a4, A5 a5) {
            return t -> accept(t, a1, a2, a3, a4, a5);
        }
    }

    interface Inquiry<T, R, X extends Exception> extends Event {

        R apply(T s) throws X;
        
        default Announcement<T, X> discardResult() {
            return this::apply;
        }
    }

    interface F1<T, A1, R, X extends Exception> extends Event {

        R apply(T t, A1 a1) throws X;

        default Inquiry<T, R, X> curry(A1 a1) {
            return t -> apply(t, a1);
        }
    }

    interface F2<T, A1, A2, R, X extends Exception> extends Event {

        R apply(T t, A1 a1, A2 a2) throws X;

        default Inquiry<T, R, X> curry(A1 a1, A2 a2) {
            return t -> apply(t, a1, a2);
        }
    }

    interface F3<T, A1, A2, A3, R, X extends Exception> extends Event {

        R apply(T t, A1 a1, A2 a2, A3 a3) throws X;

        default Inquiry<T, R, X> curry(A1 a1, A2 a2, A3 a3) {
            return t -> apply(t, a1, a2, a3);
        }
    }

    interface F4<T, A1, A2, A3, A4, R, X extends Exception> extends Event {

        R apply(T t, A1 a1, A2 a2, A3 a3, A4 a4) throws X;

        default Inquiry<T, R, X> curry(A1 a1, A2 a2, A3 a3, A4 a4) {
            return t -> apply(t, a1, a2, a3, a4);
        }
    }

    interface F5<T, A1, A2, A3, A4, A5, R, X extends Exception> extends Event {

        R apply(T t, A1 a1, A2 a2, A3 a3, A4 a4, A5 a5) throws X;

        default Inquiry<T, R, X> curry(A1 a1, A2 a2, A3 a3, A4 a4, A5 a5) {
            return t -> apply(t, a1, a2, a3, a4, a5);
        }
    }
}
