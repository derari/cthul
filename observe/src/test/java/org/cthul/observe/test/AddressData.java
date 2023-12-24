package org.cthul.observe.test;

import org.cthul.observe.Event;

public interface AddressData {
    
    void setStreet(String street);
    
    void setCity(String city);
    
    interface Events extends AddressData, Event.Definitions {
        
        @Override
        default void setStreet(String street) {
            herald().announce(AddressData.class, Event.c(AddressData::setStreet, street));
        }

        @Override
        default void setCity(String city) {
            herald().announce(AddressData.class, Event.c(AddressData::setCity, city));
        }
    }
}
