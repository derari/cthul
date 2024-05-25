package org.cthul.observe.test;

import org.cthul.observe.*;

public interface AddressData {
    
    void setStreet(String street);
    
    void setCity(String city);
    
    interface Events extends AddressData, Herald.EventDefinitions {
        
        @Override
        default void setStreet(String street) {
            herald().announce(AddressData.class, o -> o.setStreet(street));
        }

        @Override
        default void setCity(String city) {
            herald().announce(AddressData.class, o -> o.setCity(city));
        }
    }
}
