package org.cthul.observe.test;

import org.cthul.observe.Notifier;

public interface NameData {
    
    void setFirstName(String first);
    
    void setLastName(String last);
    
    interface Notifications extends NameData, Notifier.Notifications {
        
        @Override
        default void setFirstName(String first) {
            notifier().accept(NameData.class, NameData::setFirstName, first);
        }

        @Override
        default void setLastName(String last) {
            notifier().accept(NameData.class, NameData::setLastName, last);
        }
    }
}
