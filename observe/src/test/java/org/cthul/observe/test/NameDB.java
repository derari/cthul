package org.cthul.observe.test;

import org.cthul.observe.Herald;

public interface NameDB {

    NameData getName(int i);

    static Events events(Herald herald) {
        return () -> herald;
    }

    interface Events extends NameDB, Herald.EventDefinitions {

        @Override
        default NameData getName(int i) {
            return herald().inquire(NameDB.class, NameData.class, o -> o.getName(i));
        }
    }
}
