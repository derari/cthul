package org.cthul.observe;

import java.util.ArrayList;
import java.util.List;
import org.cthul.observe.test.AddressData;
import org.cthul.observe.test.NameDB;
import org.cthul.observe.test.NameData;
import org.cthul.observe.test.PersonData;
import org.junit.jupiter.api.Test;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class BasicObservableTest {

    public BasicObservableTest() {
    }

    BasicObservable observable = new BasicObservable();

    @Test
    public void testMulticast() {
        PersonDataLogger logger = new PersonDataLogger();
        NameModel name = new NameModel();

        observable.addInterface(PersonData::notifications);
        observable.addObservers(logger, name);
        PersonData notifier = observable.getNotifier(PersonData.class);
        notifier.setFirstName("Bob");
        notifier.setLastName("Loblaw");
        notifier.setCity("Berlin");

        assertThat(name.getFullName(), is("Bob Loblaw"));
        assertThat(logger.getLog(), contains("first name Bob", "last name Loblaw", "city Berlin"));
    }


    @Test
    public void testNestedMulticast() {
        PersonDataLogger logger = new PersonDataLogger();
        NameModel name0 = new NameModel();
        name0.setFirstName("Alice");
        NameModel name1 = new NameModel();
        name1.setFirstName("Bob");
        NameDB nameDB = asList(name0, name1)::get;

        observable.addInterface(NameDB::notifications);
        observable.addInterface(PersonData::notifications);
        observable.addObservers(logger, nameDB);

        NameDB notifier = observable.getNotifier(NameDB.class);
        notifier.getName(0).setLastName("Noi");
        notifier.getName(1).setLastName("Loblaw");

        assertThat(name0.getFullName(), is("Alice Noi"));
        assertThat(name1.getFullName(), is("Bob Loblaw"));
        assertThat(logger.getLog(), contains("last name Noi", "last name Loblaw"));
    }

    @Test
    public void testNestedMulticastWithDuplication() {
        NameDBLogger logger = new NameDBLogger();

        observable.addInterface(NameDB::notifications);
        observable.addInterface(PersonData::notifications);
        observable.addObserver(logger);

        NameDB notifier = observable.getNotifier(NameDB.class);
        notifier.getName(0).setLastName("Noi");
        assertThat(logger.getLog(), contains("last name Noi"));
    }

    @Test
    public void testInclude() {
        PersonDataLogger logger = new PersonDataLogger();

        observable.addInterface(PersonData::notifications);
        observable.addObserver(logger)
                .include(NameData.class);

        PersonData notifier = observable.getNotifier(PersonData.class);
        notifier.setFirstName("Bob");
        notifier.setCity("Berlin");

        assertThat(logger.getLog(), hasItem("first name Bob"));
        assertThat(logger.getLog(), not(hasItem("city Berlin")));
    }

    @Test
    public void testExclude() {
        PersonDataLogger logger = new PersonDataLogger();

        observable.addInterface(PersonData::notifications);
        observable.addObserver(logger)
                .exclude(AddressData.class);

        PersonData notifier = observable.getNotifier(PersonData.class);
        notifier.setFirstName("Bob");
        notifier.setCity("Berlin");

        assertThat(logger.getLog(), hasItem("first name Bob"));
        assertThat(logger.getLog(), not(hasItem("city Berlin")));
    }

    @Test
    public void testIncludeExclude() {
        PersonDataLogger logger = new PersonDataLogger();

        observable.addInterface(PersonData::notifications);
        observable.addObserver(logger)
                .include(PersonData.class)
                .exclude(AddressData.class);

        PersonData notifier = observable.getNotifier(PersonData.class);
        notifier.setFirstName("Bob");
        notifier.setCity("Berlin");

        assertThat(logger.getLog(), hasItem("first name Bob"));
        assertThat(logger.getLog(), not(hasItem("city Berlin")));
    }

    private static class PersonDataLogger implements PersonData {

        final List<String> log = new ArrayList<>();

        @Override
        public void setFirstName(String first) {
            log.add("first name " + first);
        }

        @Override
        public void setLastName(String last) {
            log.add("last name " + last);
        }

        @Override
        public void setStreet(String street) {
            log.add("street " + street);
        }

        @Override
        public void setCity(String city) {
            log.add("city " + city);
        }

        public List<String> getLog() {
            return log;
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
