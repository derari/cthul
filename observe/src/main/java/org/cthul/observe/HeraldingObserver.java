package org.cthul.observe;

public class HeraldingObserver implements Observer {

    private final Herald herald;

    public HeraldingObserver(Herald herald) {
        this.herald = herald;
    }

    @Override
    public <O, X extends Exception> void notify(Class<O> type, Event.Announcement<O, X> event) throws X {
        var typed = herald.as(type);
        if (typed != null) {
            event.announceTo(typed);
        }
    }

    @Override
    public <O, R, X extends Exception> R notify(Class<O> type, Event.Inquiry<O, R, X> event) throws X {
        var typed = herald.as(type);
        return typed == null ? null : event.inquireFrom(typed);
    }

    @Override
    public int hashCode() {
        return herald.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof HeraldingObserver ho) {
            return herald.equals(ho.herald);
        }
        return herald.equals(obj);
    }

    @Override
    public String toString() {
        return herald.toString();
    }
}
