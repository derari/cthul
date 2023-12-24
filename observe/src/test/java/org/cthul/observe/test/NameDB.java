package org.cthul.observe.test;

import org.cthul.observe.Event;
import org.cthul.observe.Herald;

public interface NameDB {

    NameData getName(int i);

    static Events events(Herald herald) {
        return () -> herald;
    }

    interface Events extends NameDB, Event.Definitions {

        @Override
        default NameData getName(int i) {
            return herald().enquire(NameDB.class, NameData.class, Event.f(NameDB::getName, i));
        }
    }
}
