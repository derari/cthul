package org.cthul.observe;

public class HeraldingObserver implements Observer {

    private final Herald herald;

    public HeraldingObserver(Herald herald) {
        this.herald = herald;
    }

    @Override
    public <O, X extends Exception> void notify(Class<O> type, Event.Announcement<O, X> event) throws X {
        herald.announce(type, event);
    }

    @Override
    public <O, R, X extends Exception> R notify(Class<O> type, Event.Inquiry<O, R, X> event) throws X {
        return event.apply(herald.as(type));
    }

    @Override
    public int hashCode() {
        return herald.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof HeraldingObserver to) {
            return herald.equals(to.herald);
        }
        if (obj instanceof Observer o) {
            return o.equals(herald);
        }
        return herald.equals(obj);
    }
}
