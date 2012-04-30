package org.cthul.matchers.fluent.gen.iface;

import java.util.Collection;
import java.util.List;
import org.cthul.matchers.ContainsPattern;
import org.hamcrest.Matcher;
import org.hamcrest.core.Is;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.cthul.matchers.fluent.CoreFluents.*;
import static org.hamcrest.Matchers.*;

/**
 *
 * @author Arian Treffer
 */
public class FluentMethodCollectorTest extends InterfaceGeneratorTestBase {
    
    public FluentMethodCollectorTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Test
    public void returnType_is_bound_matcher() {
        List<FluentMethod> methods = getMethods(StringMatcherResult.class);
        assertMethodFound(methods, "string", String.class);
    }
    
    @Test
    public void returnType_is_unbound_matcher() {
        List<FluentMethod> methods = getMethods(UnboundMatcherResult.class);
        assertMethodFound(methods, "object", Object.class);
    }
    
    @Test
    public void returnType_is_semibound_matcher() {
        List<FluentMethod> methods = getMethods(SemiboundMatcherResult.class);
        assertMethodFound(methods, "throwable", Throwable.class);
    }
    
    @Test
    public void returnType_is_bound_subclass() {
        List<FluentMethod> methods = getMethods(MatcherSubclassResult.class);
        assertMethodFound(methods, "string", String.class);
    }
    
    @Test
    public void returnType_is_unbound_subclass() {
        List<FluentMethod> methods = getMethods(UnboundSubclassResult.class);
        assertMethodFound(methods, "object", Object.class);
    }
    
    @Test
    public void returnType_is_semibound_subclass() {
        List<FluentMethod> methods = getMethods(SemiboundSubclassResult.class);
        assertMethodFound(methods, "throwable", Throwable.class);
    }
    
    @Test
    public void returnType_is_genericType_matcher() {
        List<FluentMethod> methods = getMethods(GenericTypeMatcherResult.class);
        assertMethodFound(methods, "collection", "java.util.Collection<java.lang.String>");
    }
    
    @Test
    public void returnType_is_unbound_genericType_matcher() {
        List<FluentMethod> methods = getMethods(UnboundGenericTypeMatcherResult.class);
        assertMethodFound(methods, "collection", "java.util.Collection<java.lang.Object>");
    }
    
    private void assertMethodFound(List<FluentMethod> methods, String name, Class itemType) {
        assertMethodFound(methods, name, itemType.getCanonicalName());
    }
    
    private void assertMethodFound(List<FluentMethod> methods, String name, String itemType) {
        assertThat(methods)._(hasSize(1));
        FluentMethod mStrings = methods.get(0);
        assertThat(mStrings.getName()).is(equalTo(name));
        assertThat(mStrings.getItemType()).is(notNullValue());
        assertThat(mStrings.getItemType().getGenericValue())
                   .is(notNullValue())
                   .and(equalTo(itemType));
    }
    
    private static class StringMatcherResult {
        public static Matcher<String> string() { return null; }
    }
    
    private static class UnboundMatcherResult {
        public static <T> Matcher<T> object() { return null; }
    }
    
    private static class SemiboundMatcherResult {
        public static <T extends Throwable> Matcher<T> throwable() { return null; }
    }
    
    private static class MatcherSubclassResult {
        public static ContainsPattern string() { return null; }
    }
    
    private static class UnboundSubclassResult {
        public static <T> Is<T> object() { return null; }
    }
    
    private static class SemiboundSubclassResult {
        public static <T extends Throwable> Is<T> throwable() { return null; }
    }
    
    private static class GenericTypeMatcherResult {
        public static Matcher<Collection<String>> collection() { return null; }
    }
    
    private static class UnboundGenericTypeMatcherResult {
        public static <T> Matcher<Collection<T>> collection() { return null; }
    }
    
}
