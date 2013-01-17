package org.cthul.matchers.chain;

import org.hamcrest.StringDescription;
import org.junit.Test;
import static org.cthul.matchers.chain.OrChainMatcher.either;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class OrChainMatcherTest {

    @Test
    public void test_matching() {
        assertThat(6, is(either(lessThan(1)).or(greaterThan(5))));
        assertThat(3, not(either(lessThan(1)).or(greaterThan(5))));
    }
    
    @Test
    public void test_description() {
        String s = either(lessThan(1)).or(greaterThan(5)).toString();
        assertThat(s, is("a value less than <1> or a value greater than <5>"));
    }
    
    @Test
    public void test_mismatch_description() {
        StringDescription desc = new StringDescription();
        either(lessThan(1)).or(greaterThan(5)).describeMismatch(1, desc);
        assertThat(desc.toString(), is("<1> was equal to <1> and <1> was less than <5>"));
    }
}
