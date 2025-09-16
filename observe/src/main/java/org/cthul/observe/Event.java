package org.cthul.observe;

@SuppressWarnings({"unused", "java:S1452"})
public interface Event<T, X extends Exception> {

    static <T, X extends Exception> Announcement<T, X> a(Announcement<T, X> event) {
        if (event instanceof Adapter<T, ?, X> adapter) return adapter;
        return new Adapter<>(event);
    }
    
    static <T, R, X extends Exception> Inquiry<T, R, X> i(Inquiry<T, R, X> event) {
        if (event instanceof Adapter<T, R, X> adapter) return adapter;
        return new Adapter<>(event);
    }

    Announcement<T, X> asAnnouncement();

    Inquiry<T, ?, X> asInquiry();

    interface Announcement<T, X extends Exception> extends Event<T, X> {

        void announceTo(T t) throws X;

        @Override
        default Announcement<T, X> asAnnouncement() {
            return this;
        }

        @Override
        default Inquiry<T, ?, X> asInquiry() {
            return new Adapter<>(this);
        }
    }

    interface Inquiry<T, R, X extends Exception> extends Event<T, X> {

        R inquireFrom(T t) throws X;
        
        @Override
        default Announcement<T, X> asAnnouncement() {
            return new Adapter<>(this);
        }

        @Override
        default Inquiry<T, R, X> asInquiry() {
            return this;
        }
    }

    record Adapter<T, R, X extends Exception>(
                Announcement<T, X> announcement,
                Inquiry<T, R, X> inquiry
            ) implements Announcement<T, X>, Inquiry<T, R, X> {

        public Adapter(Announcement<T, X> announcement) {
            this(announcement, t -> { announcement.announceTo(t); return null; });
        }

        public Adapter(Inquiry<T, R, X> inquiry) {
            this(inquiry::inquireFrom, inquiry);
        }

        @Override
        public void announceTo(T t) throws X {
            announcement.announceTo(t);
        }

        @Override
        public R inquireFrom(T t) throws X {
            return inquiry.inquireFrom(t);
        }

        @Override
        public Announcement<T, X> asAnnouncement() {
            return this;
        }

        @Override
        public Inquiry<T, R, X> asInquiry() {
            return this;
        }
    }
}
