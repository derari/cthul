package org.cthul.observe;

import org.cthul.observe.test.AddressData;
import org.cthul.observe.test.NameDB;
import org.cthul.observe.test.NameData;
import org.cthul.observe.test.PersonData;
import org.junit.jupiter.api.Test;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class ObservableTest {
    
    public ObservableTest() {
    }
    
    Observable observable = new Observable();
    
    @Test
    public void testMulticast() {
        PersonDataLogger logger = new PersonDataLogger();
        NameModel name = new NameModel();
        
        observable.addInterface(PersonData.Notifications.class, n -> () -> n);
        observable.addObserver(logger);
        observable.addObserver(name);
        
        PersonData personData = observable.getNotifier(PersonData.class);
        personData.setFirstName("Bob");
        personData.setLastName("Loblaw");
        personData.setCity("Berlin");
        
        assertThat(name.getFullName(), is("Bob Loblaw"));
        assertThat(logger.getLog(), containsString("first name Bob"));
        assertThat(logger.getLog(), containsString("last name Loblaw"));
        assertThat(logger.getLog(), containsString("city Berlin"));
    }
    
    @Test
    public void testNestedMulticast() {
        PersonDataLogger logger = new PersonDataLogger();
        NameModel name0 = new NameModel();
        name0.setFirstName("Alice");
        NameModel name1 = new NameModel();
        name1.setFirstName("Bob");
        NameDB nameDB = asList(name0, name1)::get;
        
        observable.addInterface(NameDB.Notifications.class, n -> () -> n);
        observable.addInterface(PersonData.Notifications.class, n -> () -> n);
        observable.addObserver(logger);
        observable.addObserver(nameDB);
        
        NameDB actual = observable.getNotifier(NameDB.class);
        actual.getName(0).setLastName("Noi");
        actual.getName(1).setLastName("Loblaw");
        
        assertThat(name0.getFullName(), is("Alice Noi"));
        assertThat(name1.getFullName(), is("Bob Loblaw"));
        assertThat(logger.getLog(), containsString("last name Noi"));
        assertThat(logger.getLog(), containsString("last name Loblaw"));
    }
    
    @Test
    public void testNestedMulticastWithDuplication() {
        NameDBLogger logger = new NameDBLogger();
        
        observable.addInterface(NameDB.Notifications.class, n -> () -> n);
        observable.addInterface(PersonData.Notifications.class, n -> () -> n);
        observable.addObserver(logger);
        
        NameDB actual = observable.getNotifier(NameDB.class);
        actual.getName(0).setLastName("Noi");
        assertThat(logger.getLog(), is("last name Noi\n"));
    }
    
    @Test
    public void testInclude() {
        PersonDataLogger logger = new PersonDataLogger();
        
        observable.addInterface(PersonData.Notifications.class, n -> () -> n);
        observable.addObserver(logger)
                .include(NameData.class);
        
        PersonData personData = observable.getNotifier(PersonData.class);
        personData.setFirstName("Bob");
        personData.setCity("Berlin");
        
        assertThat(logger.getLog(), containsString("first name Bob"));
        assertThat(logger.getLog(), not(containsString("city Berlin")));
    }
    
    @Test
    public void testExclude() {
        PersonDataLogger logger = new PersonDataLogger();
        
        observable.addInterface(PersonData.Notifications.class, n -> () -> n);
        observable.addObserver(logger)
                .exclude(AddressData.class);
        
        PersonData personData = observable.getNotifier(PersonData.class);
        personData.setFirstName("Bob");
        personData.setCity("Berlin");
        
        assertThat(logger.getLog(), containsString("first name Bob"));
        assertThat(logger.getLog(), not(containsString("city Berlin")));
    }
    
    @Test
    public void testIncludeExclude() {
        PersonDataLogger logger = new PersonDataLogger();
        
        observable.addInterface(PersonData.Notifications.class, n -> () -> n);
        observable.addObserver(logger)
                .include(PersonData.class)
                .exclude(AddressData.class);
        
        PersonData personData = observable.getNotifier(PersonData.class);
        personData.setFirstName("Bob");
        personData.setCity("Berlin");
        
        assertThat(logger.getLog(), containsString("first name Bob"));
        assertThat(logger.getLog(), not(containsString("city Berlin")));
    }
    
    private static class PersonDataLogger implements PersonData {

        final StringBuilder log = new StringBuilder();
        
        @Override
        public void setFirstName(String first) {
            log.append("first name ").append(first).append("\n");
        }

        @Override
        public void setLastName(String last) {
            log.append("last name ").append(last).append("\n");
        }

        @Override
        public void setStreet(String street) {
            log.append("street ").append(street).append("\n");
        }

        @Override
        public void setCity(String city) {
            log.append("city ").append(city).append("\n");
        }

        public String getLog() {
            return log.toString();
        }
    }
    
    private static class NameModel implements NameData {
        
        private String first, last;

        @Override
        public void setFirstName(String first) {
            this.first = first;
        }

        @Override
        public void setLastName(String last) {
            this.last = last;
        }
        
        public String getFullName() {
            return first + " " + last;
        }
    }
    
    private static class NameDBLogger extends PersonDataLogger implements NameDB {

        @Override
        public NameData getName(int i) {
            return this;
        }
    }
}
