package org.cthul.matchers.diagnose;

import org.hamcrest.StringDescription;
import org.junit.Test;
import static org.cthul.matchers.chain.AndChainMatcher.both;
import static org.cthul.matchers.chain.OrChainMatcher.either;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.fail;

public class NestedMatcherTest {

    @Test
    public void test_precedenced_description_1() {
        StringDescription desc = new StringDescription();
        either(is(6)).or(both(greaterThan(3)).and(lessThan(5))).describeTo(desc);
        assertThat(desc.toString(), is("is <6> or a value greater than <3> and a value less than <5>"));
    }
    
    @Test
    public void test_java_precedence() {
        int x = 6;
        if (x == 6 || (x > 3 && x < 5)) {
            // expected
        } else {
            fail("Java is broken");
        }
        if (x == 6 || x > 3 && x < 5) {
            // expected
        } else {
            fail("Java is broken");
        }
        if ((x == 6 || x > 3) && x < 5) {
            fail("Java is broken");
        } else {
            // expected
        }
    }

    @Test
    public void test_precedenced_description_2() {
        StringDescription desc = new StringDescription();
        both(lessThan(5)).and(either(is(6)).or(greaterThan(3))).describeTo(desc);
        assertThat(desc.toString(), is("a value less than <5> and (is <6> or a value greater than <3>)"));
    }
}
