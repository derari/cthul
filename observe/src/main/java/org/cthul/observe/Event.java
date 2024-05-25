package org.cthul.observe;

public interface Event {

    static <T, X extends Exception> Announcement<T, X> a(Announcement<T, X> event) {
        return event;
    }
    
    static <T, R, X extends Exception> Inquiry<T, R, X> i(Inquiry<T, R, X> event) {
        return event;
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

    interface Inquiry<T, R, X extends Exception> extends Event {

        R apply(T s) throws X;
        
        default Announcement<T, X> discardResult() {
            return this::apply;
        }
    }
}
