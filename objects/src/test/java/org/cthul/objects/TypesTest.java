package org.cthul.objects;

import java.util.List;
import org.junit.Test;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;

/**
 *
 */
public class TypesTest {
    
    static interface Ia {}
    static interface Ib {}
    static interface Iab extends Ia, Ib {}
    static interface Iab2 extends Ia, Ib {}
    static interface Ib2 extends Ib {}
    static class Ca implements Ia {}
    static class Cb implements Ib {}
    static class Cab extends Ca implements Iab {};
    static class CaIb extends Ca implements Ib {};
    
    @Test
    public void test_commonSuperClasses_classes() {
        List<Class<?>> l = Types.commonSuperClasses(Ca.class, Cab.class);
        assertThat(l, contains(Ca.class, Object.class, Ia.class));
    }

    @Test
    public void test_commonSuperClasses_interfaces() {
        List<Class<?>> l = Types.commonSuperClasses(Iab.class, Ib2.class);
        assertThat(l, contains((Class) Ib.class));
    }

    @Test
    public void test_commonSuperClasses_mixed() {
        List<Class<?>> l = Types.commonSuperClasses(Cab.class, Ib2.class);
        assertThat(l, contains((Class) Ib.class));
    }

    @Test
    public void test_lowestCommonSuperClasses_classes() {
        List<Class<?>> l = Types.lowestCommonSuperClasses(Ca.class, Cab.class);
        assertThat(l, contains((Class) Ca.class));
    }

    @Test
    public void test_lowestCommonSuperClasses_classes_2() {
        List<Class<?>> l = Types.lowestCommonSuperClasses(Cab.class, CaIb.class);
        assertThat(l, contains(Ca.class, Ib.class));
    }

    @Test
    public void test_lowestCommonSuperClasses_interfaces() {
        List<Class<?>> l = Types.lowestCommonSuperClasses(Iab.class, Ib2.class);
        assertThat(l, contains((Class) Ib.class));
    }

    @Test
    public void test_lowestCommonSuperClasses_mixed() {
        List<Class<?>> l = Types.lowestCommonSuperClasses(Cab.class, Iab2.class);
        assertThat(l, contains(Ia.class, Ib.class));
    }

}