package org.cthul.observe.test;

import org.cthul.observe.Event;

public interface NameData {
    
    void setFirstName(String first);
    
    void setLastName(String last);
    
    interface Events extends NameData, Event.Definitions {
        
        @Override
        default void setFirstName(String first) {
            herald().announce(NameData.class, Event.c(NameData::setFirstName, first));
        }

        @Override
        default void setLastName(String last) {
            herald().announce(NameData.class, Event.c(NameData::setLastName, last));
        }
    }
}
