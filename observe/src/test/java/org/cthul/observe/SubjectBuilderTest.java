package org.cthul.observe;

import org.cthul.observe.test.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class SubjectBuilderTest {

    SubjectBuilder subject = new SubjectBuilder();

    @Test
    void herald_multicast() {
        var logger = new PersonDataLogger();
        var name = new NameModel();

        subject.addObservers(logger, name);
        var herald = subject.getHerald();
        herald.announce(NameData.class, n -> n.setFirstName("Bob"));
        herald.announce(NameData.class, n -> n.setLastName("Loblaw"));
        herald.announce(AddressData.class, n -> n.setCity("Berlin"));

        assertThat(name.getFullName(), is("Bob Loblaw"));
        assertThat(logger.getLog(), contains("first name Bob", "last name Loblaw", "city Berlin"));
    }

    @Test
    void herald_typedMulticast() {
        var logger = new PersonDataLogger();
        var name = new NameModel();

        subject.addObservers(logger, name);
        var herald = subject.getHerald().as(PersonData::events);
        herald.setFirstName("Bob");
        herald.setLastName("Loblaw");
        herald.setCity("Berlin");

        assertThat(name.getFullName(), is("Bob Loblaw"));
        assertThat(logger.getLog(), contains("first name Bob", "last name Loblaw", "city Berlin"));
    }

    @Test
    void herald_nestedMulticast() {
        var logger = new PersonDataLogger();
        var name0 = new NameModel();
        name0.setFirstName("Alice");
        var name1 = new NameModel();
        name1.setFirstName("Bob");
        NameDB nameDB = List.of(name0, name1)::get;

        subject.getHerald().declare(NameDB::events, PersonData::events);
        subject.addObservers(logger, nameDB);

        var herald = subject.getHerald().as(NameDB.class);
        herald.getName(0).setLastName("Noi");
        herald.getName(1).setLastName("Loblaw");

        assertThat(name0.getFullName(), is("Alice Noi"));
        assertThat(name1.getFullName(), is("Bob Loblaw"));
        assertThat(logger.getLog(), contains("last name Noi", "last name Loblaw"));
    }

    @Test
    void herald_nestedMulticastWithDuplication() {
        var logger = new NameDBLogger();

        subject.getHerald().declare(NameDB::events, PersonData::events);
        subject.addObserver(logger);

        var herald = subject.getHerald().as(NameDB.class);
        herald.getName(0).setLastName("Noi");

        assertThat(logger.getLog(), contains("get 0", "last name Noi"));
    }

    @Test
    void herald_withProxy() {
        var logger = new NameDBLogger();

        subject.getHerald().declare(NameDB.class, PersonData.class);
        subject.addObserver(logger);

        var herald = subject.getHerald().as(NameDB.class);
        herald.getName(0).setLastName("Noi");

        assertThat(logger.getLog(), contains("get 0", "last name Noi"));
    }

    @Test
    void herald_implicitProxy() {
        var logger = new NameDBLogger();

        subject.addObserver(logger);

        var herald = subject.getHerald().as(NameDB.class);
        herald.getName(0).setLastName("Noi");

        assertThat(logger.getLog(), contains("get 0", "last name Noi"));
    }

    @Test
    void buildObserver_include() {
        var logger = new PersonDataLogger();

        subject.getHerald().declare(PersonData::events);
        subject.buildObserver(logger)
                .include(NameData.class);

        var herald = subject.getHerald().as(PersonData.class);
        herald.setFirstName("Bob");
        herald.setCity("Berlin");

        assertThat(logger.getLog(), hasItem("first name Bob"));
        assertThat(logger.getLog(), not(hasItem("city Berlin")));
    }

    @Test
    void buildObserver_exclude() {
        var logger = new PersonDataLogger();

        subject.getHerald().declare(PersonData::events);
        subject.buildObserver(logger)
                .exclude(AddressData.class);

        var herald = subject.getHerald().as(PersonData.class);
        herald.setFirstName("Bob");
        herald.setCity("Berlin");

        assertThat(logger.getLog(), hasItem("first name Bob"));
        assertThat(logger.getLog(), not(hasItem("city Berlin")));
    }

    @Test
    void buildObserver_includeExclude() {
        var logger = new PersonDataLogger();

        subject.getHerald().declare(PersonData::events);
        subject.buildObserver(logger)
                .include(PersonData.class)
                .exclude(AddressData.class);

        var herald = subject.getHerald().as(PersonData.class);
        herald.setFirstName("Bob");
        herald.setCity("Berlin");

        assertThat(logger.getLog(), hasItem("first name Bob"));
        assertThat(logger.getLog(), not(hasItem("city Berlin")));
    }

    @Test
    void removeObserver() {
        var logger = new PersonDataLogger();
        var herald = subject.getHerald().as(PersonData::events);

        subject.addObserver(logger);
        herald.setCity("Amsterdam");

        subject.removeObserver(logger);
        herald.setCity("Berlin");

        assertThat(logger.getLog(), hasItem("city Amsterdam"));
        assertThat(logger.getLog(), not(hasItem("city Berlin")));
    }

    static class PersonDataLogger implements PersonData {

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

    static class NameModel implements NameData {

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

    static class NameDBLogger extends PersonDataLogger implements NameDB {

        @Override
        public NameData getName(int i) {
            log.add("get " + i);
            return this;
        }
    }
}
