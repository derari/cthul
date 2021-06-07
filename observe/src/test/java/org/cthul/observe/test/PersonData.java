package org.cthul.observe.test;

public interface PersonData extends NameData, AddressData {
    
    interface Notifications extends PersonData, NameData.Notifications, AddressData.Notifications {
        
    }
}
