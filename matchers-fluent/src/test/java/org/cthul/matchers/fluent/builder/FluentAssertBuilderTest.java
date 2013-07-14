package org.cthul.matchers.fluent.builder;

import org.cthul.matchers.fluent.FluentAssert;
import org.cthul.matchers.fluent.FluentTestBase;
import org.junit.Test;
import static org.hamcrest.Matchers.*;

/**
 *
 */
public class FluentAssertBuilderTest extends FluentTestBase {
    
    @Test
    public void test_simple_match() {
        test_assertThat(3)._(lessThan(2));
        assertMismatch("greater than <2>");
    }
    
    @Test
    public void test_chained_match() {
        test_assertThat(3)
                ._(lessThan(5))
                ._(lessThan(2));
        assertMismatch("greater than <2>");
    }
    
    @Test
    public void test_negated_match() {
        test_assertThat(3)
                .not(lessThan(5))
                ._(lessThan(2));
        assertMismatch("less than <5>");
    }
    
    @Test
    public void test_message() {
        test_assertThat(3).is(equalTo(1));
        assertMismatch("was <3>");
    }
    
    @Test
    public void test_message_not() {
        test_assertThat(3).isNot(equalTo(3));
        assertMismatch("was <3>");
    }
    
    protected <T> FluentAssert<T> test_assertThat(T object) {
        return new FluentAssertBuilder<>(TEST_HANDLER, object);
    }
    
}