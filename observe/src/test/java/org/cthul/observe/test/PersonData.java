package org.cthul.observe.test;

import org.cthul.observe.Notifier;

public interface PersonData extends NameData, AddressData {

    static Notifications notifications(Notifier notifier) {
        return () -> notifier;
    }

    interface Notifications extends PersonData, NameData.Notifications, AddressData.Notifications {

    }
}
