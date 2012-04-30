package org.cthul.matchers.fluent.gen.iface;

import java.util.ArrayList;
import java.util.List;
import org.cthul.matchers.fluent.assertion.AssertionStrategy;
import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.cthul.matchers.fluent.CoreFluents.*;
import static org.hamcrest.Matchers.*;

/**
 *
 * @author Arian Treffer
 */
public class ItemTypeFilterTest extends InterfaceGeneratorTestBase {
    
    public ItemTypeFilterTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    @Before
    public void setUp() {
        super.setUp();
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void select_object() {
        List<FluentMethod> methods = getMethods(ObjStrMatchers.class);
        methods = filter(methods, Object.class);
        
        assertThat(methods).isNot(nullValue());
        assertThat(namesOf(methods))._(containsInAnyOrder("object"));
    }
    
    @Test
    public void select_string() {
        List<FluentMethod> methods = getMethods(ObjStrMatchers.class);
        methods = filter(methods, String.class);
        
        assertThat(methods).isNot(nullValue());
        assertThat(namesOf(methods))._(containsInAnyOrder("object", "string"));
    }
    
    @Test
    public void select_B() {
        List<FluentMethod> methods = getMethods(ABCMatchers.class);
        methods = filter(methods, B.class);
        
        assertThat(methods).isNot(nullValue());
        assertThat(namesOf(methods))._(containsInAnyOrder("o", "a", "b", "bi"));
    }
    
    @Test
    public void select_Bi() {
        List<FluentMethod> methods = getMethods(ABCMatchers.class);
        methods = filter(methods, Bi.class);
        
        assertThat(methods).isNot(nullValue());
        assertThat(namesOf(methods))._(containsInAnyOrder("bi"));
    }
    
    private List<FluentMethod> filter(List<FluentMethod> methods, Class type) {
        return filter(methods, classToString(type));
    }
    
    private List<FluentMethod> filter(List<FluentMethod> methods, String type) {
        ItemTypeFilter filter = new ItemTypeFilter(doc, methods);
        return filter.getMethodsForType(type);
    }
    
    private static List<String> namesOf(List<FluentMethod> methods) {
        List<String> names = new ArrayList<>(methods.size());
        for (FluentMethod m: methods) {
            names.add(m.getName());
        }
        return names;
    }
    
    private static class ObjStrMatchers {
        public static Matcher<String> string() { return null; }
        public static Matcher<Object> object() { return null; }
    }
    
    private static class ABCMatchers {
        public static Matcher<Object> o() { return null; }
        public static Matcher<A> a() { return null; }
        public static Matcher<B> b() { return null; }
        public static Matcher<Bi> bi() { return null; }
        public static Matcher<C> c() { return null; }
    }
    
    private static class A {
    }
    
    private static class B extends A implements Bi {
    }
    
    private static class C extends B {
    }
    
    private static interface Bi {
    }
    
}
