package org.cthul.observe;

import org.cthul.adapt.Adaptive;
import org.cthul.observe.test.*;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.function.BiFunction;

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
        herald.announce(NameData.class, o -> o.setFirstName("Bob"));
        herald.announce(NameData.class, o -> o.setLastName("Loblaw"));
        herald.announce(AddressData.class, o -> o.setCity("Berlin"));

        assertThat(name.getFullName(), is("Bob Loblaw"));
        assertThat(logger.getLog(), contains("first name Bob", "last name Loblaw", "city Berlin"));
    }

    @Test
    void herald_typedMulticast() {
        var logger = new PersonDataLogger();
        var name = new NameModel();
        subject.addObservers(logger, name);

        var herald = subject.getHerald().as(PersonData::herald);
        herald.setFirstName("Bob");
        herald.setLastName("Loblaw");
        herald.setCity("Berlin");

        assertThat(name.getFullName(), is("Bob Loblaw"));
        assertThat(logger.getLog(), contains("first name Bob", "last name Loblaw", "city Berlin"));
    }

    @Test
    void herald_nestedMulticast() {
        var logger = new PersonDataLogger();
        var name0 = new NameModel("Alice", "X");
        var name1 = new NameModel("Bob", "X");
        NameDB nameDB = Map.of(0, name0, 1, name1)::get;
        subject.addObservers(logger, nameDB);

        subject.getHerald().declare(NameDB::herald, PersonData::herald);
        var herald = subject.getHerald().as(NameDB.class);
        herald.getName(0).setLastName("Noi");
        herald.getName(1).setLastName("Loblaw");
        herald.getName(2).setLastName("Doe");

        assertThat(name0.getFullName(), is("Alice Noi"));
        assertThat(name1.getFullName(), is("Bob Loblaw"));
        assertThat(logger.getLog(), contains("last name Noi", "last name Loblaw", "last name Doe"));
    }

    @Test
    void herald_nestedMulticastWithDuplication() {
        var logger = new NameDBLogger();
        subject.addObserver(logger);

        subject.getHerald().declare(NameDB::herald, PersonData::herald);
        var herald = subject.as(NameDB.class);
        herald.getName(0).setLastName("Noi");

        assertThat(logger.getLog(), contains("get 0", "last name Noi"));
    }

    @Test
    void herald_withProxy() {
        var logger = new NameDBLogger();
        subject.addObserver(logger);

        subject.getHerald().declare(NameDB.class, PersonData.class);
        var herald = subject.as(NameDB.class);
        herald.getName(0).setLastName("Noi");

        assertThat(logger.getLog(), contains("get 0", "last name Noi"));
    }

    @Test
    void herald_implicitProxy() {
        var logger = new NameDBLogger();
        subject.addObserver(logger);

        var herald = subject.as(NameDB.class);
        herald.getName(0).setLastName("Noi");

        assertThat(logger.getLog(), contains("get 0", "last name Noi"));
    }

    @Test
    void buildObserver_include() {
        var logger = new PersonDataLogger();

        subject.buildObserver(logger)
                .include(NameData.class);

        var herald = subject.getHerald().as(PersonData::herald);
        herald.setFirstName("Bob");
        herald.setCity("Berlin");

        assertThat(logger.getLog(), hasItem("first name Bob"));
        assertThat(logger.getLog(), not(hasItem("city Berlin")));
    }

    @Test
    void buildObserver_exclude() {
        var logger = new PersonDataLogger();

        subject.buildObserver(logger)
                .exclude(AddressData.class);

        var herald = subject.getHerald().as(PersonData::herald);
        herald.setFirstName("Bob");
        herald.setCity("Berlin");

        assertThat(logger.getLog(), hasItem("first name Bob"));
        assertThat(logger.getLog(), not(hasItem("city Berlin")));
    }

    @Test
    void buildObserver_includeExclude() {
        var logger = new PersonDataLogger();

        subject.buildObserver(logger)
                .include(PersonData.class)
                .exclude(AddressData.class);

        var herald = subject.getHerald().as(PersonData::herald);
        herald.setFirstName("Bob");
        herald.setCity("Berlin");

        assertThat(logger.getLog(), hasItem("first name Bob"));
        assertThat(logger.getLog(), not(hasItem("city Berlin")));
    }

    @Test
    void removeObserver() {
        var logger = new PersonDataLogger();
        var herald = subject.getHerald().as(PersonData::herald);

        var sub = subject.addObserver(logger);
        herald.setCity("Amsterdam");

        sub.unsubscribe();
        herald.setCity("Berlin");

        assertThat(logger.getLog(), hasItem("city Amsterdam"));
        assertThat(logger.getLog(), not(hasItem("city Berlin")));
    }

    @Test
    void chained() {
        var subject2 = new SubjectBuilder();
        var logger = new PersonDataLogger();
        subject2.addObserver(logger);
        subject.addObserver(subject2);

        var herald = subject.getHerald().as(PersonData::herald);
        herald.setCity("Amsterdam");

        assertThat(logger.getLog(), hasItem("city Amsterdam"));
    }

    @Test
    void asAdaptive() {
        var logger = new PersonDataLogger();
        subject.addObserver(logger);

        var herald = subject.as(AdaptivePersonData.class);

        var actual = herald.herald();
        assertThat(actual, is(subject.getHerald()));

        herald.as(NameData.class).setFirstName("Alice");
        assertThat(logger.getLog(), hasItem("first name Alice"));
    }

    @Test
    void withDefaultMethods() {
        var logger = new PersonDataLogger();
        subject.addObserver(logger);

        subject.getHerald().declare(AdaptivePersonData.class);
        subject.as(AddressData.class).setCity("Berlin");

        assertThat(logger.getLog(), hasItem("city Big city of Berlin"));
    }

    @Test
    void withoutDefaultMethods() {
        class PL2 extends PersonDataLogger implements AdaptivePersonData {
            @Override
            public <T> T as(Class<T> clazz, BiFunction<? super Herald, ? super Class<T>, ? extends T> ifUndeclared) {
                return null;
            }
            @Override
            public Herald herald() {
                return null;
            }
        }

        var logger = new PL2();
        subject.addObserver(logger);

        subject.getHerald().declare(AdaptivePersonData.class);
        subject.getHerald().setDefaultAdapter(HeraldInvocationProxy.castOrProxyWithoutDefaults());
        subject.as(AddressData.class).setCity("Berlin");

        assertThat(logger.getLog(), hasItem("city Berlin"));
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

        public NameModel() {
        }

        public NameModel(String first, String last) {
            this.first = first;
            this.last = last;
        }

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
        public NameData getName(int id) {
            log.add("get " + id);
            return this;
        }
    }

    interface AdaptivePersonData extends PersonData, Herald.EventAdapter, Adaptive.Typed<Herald> {

        @Override
        default void setCity(String city) {
            herald().announce(AddressData.class, o -> o.setCity("Big city of " + city));
        }
    }
}
