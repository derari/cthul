package org.cthul.matchers.fluent.builder;

import org.cthul.matchers.fluent.FluentAssert;
import org.cthul.matchers.fluent.FluentMatcher;
import org.cthul.matchers.fluent.FluentTestBase;
import org.junit.Test;
import static org.hamcrest.Matchers.*;

/**
 *
 */
public class FluentMatcherBuilderTest extends FluentTestBase {
    
    @Test
    public void test_simple_match() {
        test_assertThat(3, matcherI()._(lessThan(2)));
        
        assertMismatch("greater than <2>");
    }
    
    @Test
    public void test_chained_match() {
        test_assertThat(3, matcherI()
                ._(lessThan(5))
                .and(lessThan(2)));
        assertMismatch("greater than <2>");
    }
    
    @Test
    public void test_negated_match() {
        test_assertThat(3, matcherI()
                .not(lessThan(5))
                .and(lessThan(2)));
        assertMismatch("less than <5>");
    }
    
    @Test
    public void test_message_with_is() {
        test_assertThat(3, matcherI()
                .is(lessThan(2)));
        assertMismatch("greater than <2>");
    }
    
    @Test
    public void test_description() {
        
    }
    
    protected <T> FluentMatcher<T, T> matcher(Class<T> c) {
        return FluentMatcherBuilder.match();
    }
    
    protected FluentMatcher<Integer, Integer> matcherI() {
        return FluentMatcherBuilder.match();
    }
    
    protected <T> FluentAssert<T> test_assertThat(T object) {
        return new FluentAssertBuilder<>(TEST_HANDLER, object);
    }
    
}