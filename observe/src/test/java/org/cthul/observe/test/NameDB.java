package org.cthul.observe.test;

import org.cthul.observe.Herald;

public interface NameDB {

    NameData getName(int id);

    static HeraldAdapter herald(Herald herald) {
        return () -> herald;
    }

    interface HeraldAdapter extends NameDB, Herald.EventAdapter {

        @Override
        default NameData getName(int id) {
            return herald().inquire(NameDB.class, NameData.class, o -> o.getName(id));
        }
    }
}
