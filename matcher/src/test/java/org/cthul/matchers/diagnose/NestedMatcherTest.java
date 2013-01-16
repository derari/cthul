package org.cthul.matchers.diagnose;

import org.hamcrest.StringDescription;
import org.junit.Test;
import static org.cthul.matchers.chain.AndChainMatcher.both;
import static org.cthul.matchers.chain.OrChainMatcher.either;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class NestedMatcherTest {

    @Test
    public void test_precedenced_description_1() {
        StringDescription desc = new StringDescription();
        either(lessThan(1)).or(both(greaterThan(5)).and(lessThan(10))).describeTo(desc);
        assertThat(desc.toString(), is("a value less than <1> or a value greater than <5> and a value less than <10>"));
    }

    @Test
    public void test_precedenced_description_2() {
        StringDescription desc = new StringDescription();
        both(greaterThan(1)).and(either(greaterThan(10)).or(lessThan(5))).describeTo(desc);
        assertThat(desc.toString(), is("a value greater than <1> and (a value greater than <10> or a value less than <5>)"));
    }
}
