package org.cthul.observe.test;

import org.cthul.observe.Notifier;

public interface AddressData {
    
    void setStreet(String street);
    
    void setCity(String city);
    
    interface Notifications extends AddressData, Notifier.Notifications {
        
        @Override
        default void setStreet(String street) {
            notifier().accept(AddressData.class, AddressData::setStreet, street);
        }

        @Override
        default void setCity(String city) {
            notifier().accept(AddressData.class, AddressData::setCity, city);
        }
    }
}
