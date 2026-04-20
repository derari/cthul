package org.cthul.observe;

import org.cthul.adapt.Adaptive;

public class AdaptingObserver implements Observer {

    private final Adaptive target;

    public AdaptingObserver(Adaptive target) {
        this.target = target;
    }

    @Override
    public <O, X extends Exception> void notify(Class<O> type, Event.Announcement<O, X> event) throws X {
        var typed = target.as(type);
        if (typed != null) {
            event.announceTo(typed);
        }
    }

    @Override
    public <O, R, X extends Exception> R notify(Class<O> type, Event.Inquiry<O, R, X> event) throws X {
        var typed = target.as(type);
        return typed == null ? null : event.inquireFrom(typed);
    }

    @Override
    public int hashCode() {
        return target.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AdaptingObserver ho) {
            return target.equals(ho.target);
        }
        return target.equals(obj);
    }

    @Override
    public String toString() {
        return target.toString();
    }
}
