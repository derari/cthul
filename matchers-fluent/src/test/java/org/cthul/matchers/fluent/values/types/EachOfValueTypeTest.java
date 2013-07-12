package org.cthul.matchers.fluent.values.types;

import java.util.Arrays;
import java.util.List;
import org.cthul.matchers.fluent.FluentAssert;
import org.cthul.matchers.fluent.FluentTestBase;
import org.cthul.matchers.fluent.builder.FluentAssertBuilder;
import org.cthul.matchers.fluent.values.MatchValueType;
import org.cthul.matchers.fluent.values.MatchingObject;
import static org.hamcrest.Matchers.*;
import static org.cthul.matchers.fluent.values.types.EachOfValueType.*;
import org.junit.Test;

/**
 *
 */
public class EachOfValueTypeTest extends FluentTestBase {
        
    @Test
    public void test_simple_match() {
        test_assertThat(eachOf(1, 3, 5))._(lessThan(2));
        assertMismatch("<3> was greater than <2>");
    }
    
    @Test
    public void test_simple_match_success() {
        test_assertThat(eachOf(1, 3, 5))._(lessThan(10));
        assertSuccess();
    }
    
    @Test
    public void test_chained_match() {
        test_assertThat(eachOf(1, 3, 4))
                ._(lessThan(5))
                ._(lessThan(2));
        assertMismatch("<3> was greater than <2>");
    }
    
    @Test
    public void test_as_matcher() {
        
    }
    
    @Test
    public void test_chained_each() {
        List<Integer> list = Arrays.asList(1, 3, 5);
        MatchValueType<Iterable<Integer>, Integer> each = null;
        test_assertThat(list)
                ._(eachInt()).is(greaterThan(0));
        
    }
    
    MatchValueType<Iterable<? extends Integer>, Integer> eachInt() {
        return eachOf();
    }
    
    <T> MatchValueType<Object, T> get(Class<T> c, String p) {
        return null;
    }
 
    protected <T> FluentAssert<T> test_assertThat(MatchingObject<T> object) {
        return new FluentAssertBuilder<>(TEST_HANDLER, object);
    }
    
    protected <T> FluentAssert<T> test_assertThat(T object) {
        return new FluentAssertBuilder<>(TEST_HANDLER, object);
    }
    
}