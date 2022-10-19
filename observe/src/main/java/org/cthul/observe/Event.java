package org.cthul.observe;

public interface Event {
    
    interface Definitions {
        
        Herald herald();
    }
    
    interface C0<S, X extends Exception> extends Event {

        void accept(S s) throws X;

        default F0<S, Void, X> mapToNull() {
            return s -> {
                accept(s);
                return null;
            };
        }
    }

    interface C1<S, A1, X extends Exception> extends Event {

        void accept(S s, A1 a1) throws X;

        default C0<S, X> curry(A1 a1) {
            return s -> accept(s, a1);
        }
    }

    interface C2<S, A1, A2, X extends Exception> extends Event {

        void accept(S s, A1 a1, A2 a2) throws X;

        default C0<S, X> curry(A1 a1, A2 a2) {
            return s -> accept(s, a1, a2);
        }
    }

    interface C3<S, A1, A2, A3, X extends Exception> extends Event {

        void accept(S s, A1 a1, A2 a2, A3 a3) throws X;

        default C0<S, X> curry(A1 a1, A2 a2, A3 a3) {
            return s -> accept(s, a1, a2, a3);
        }
    }

    interface C4<S, A1, A2, A3, A4, X extends Exception> extends Event {

        void accept(S s, A1 a1, A2 a2, A3 a3, A4 a4) throws X;

        default C0<S, X> curry(A1 a1, A2 a2, A3 a3, A4 a4) {
            return s -> accept(s, a1, a2, a3, a4);
        }
    }

    interface C5<S, A1, A2, A3, A4, A5, X extends Exception> extends Event {

        void accept(S s, A1 a1, A2 a2, A3 a3, A4 a4, A5 a5) throws X;

        default C0<S, X> curry(A1 a1, A2 a2, A3 a3, A4 a4, A5 a5) {
            return s -> accept(s, a1, a2, a3, a4, a5);
        }
    }

    interface F0<S, R, X extends Exception> extends Event {

        R apply(S s) throws X;
        
        default C0<S, X> discardResult() {
            return this::apply;
        }
    }

    interface F1<S, A1, R, X extends Exception> extends Event {

        R apply(S s, A1 a1) throws X;

        default F0<S, R, X> curry(A1 a1) {
            return s -> apply(s, a1);
        }
    }

    interface F2<S, A1, A2, R, X extends Exception> extends Event {

        R apply(S s, A1 a1, A2 a2) throws X;

        default F0<S, R, X> curry(A1 a1, A2 a2) {
            return s -> apply(s, a1, a2);
        }
    }

    interface F3<S, A1, A2, A3, R, X extends Exception> extends Event {

        R apply(S s, A1 a1, A2 a2, A3 a3) throws X;

        default F0<S, R, X> curry(A1 a1, A2 a2, A3 a3) {
            return s -> apply(s, a1, a2, a3);
        }
    }

    interface F4<S, A1, A2, A3, A4, R, X extends Exception> extends Event {

        R apply(S s, A1 a1, A2 a2, A3 a3, A4 a4) throws X;

        default F0<S, R, X> curry(A1 a1, A2 a2, A3 a3, A4 a4) {
            return s -> apply(s, a1, a2, a3, a4);
        }
    }

    interface F5<S, A1, A2, A3, A4, A5, R, X extends Exception> extends Event {

        R apply(S s, A1 a1, A2 a2, A3 a3, A4 a4, A5 a5) throws X;

        default F0<S, R, X> curry(A1 a1, A2 a2, A3 a3, A4 a4, A5 a5) {
            return s -> apply(s, a1, a2, a3, a4, a5);
        }
    }
}
