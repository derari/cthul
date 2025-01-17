package org.cthul.observe.test;

import org.cthul.observe.Herald;

public interface PersonData extends NameData, AddressData {

    static HeraldAdapter herald(Herald herald) {
        return () -> herald;
    }

    interface HeraldAdapter extends PersonData, NameData.HeraldAdapter, AddressData.HeraldAdapter {
    }
}
