package org.cthul.observe.test;

import org.cthul.observe.Herald;

public interface PersonData extends NameData, AddressData {

    static Events events(Herald herald) {
        return () -> herald;
    }

    interface Events extends PersonData, NameData.Events, AddressData.Events {
    }
}
