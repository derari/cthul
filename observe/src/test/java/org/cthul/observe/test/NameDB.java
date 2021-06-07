package org.cthul.observe.test;

import org.cthul.observe.Notifier;

public interface NameDB {

    NameData getName(int i);
    
    interface Notifications extends NameDB, Notifier.Notifications {
        
        default NameData getName(int i) {
            return notifier().apply(NameDB.class, NameData.class, NameDB::getName, i);
        }
    }
}
