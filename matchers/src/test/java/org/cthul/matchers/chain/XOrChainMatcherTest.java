package org.cthul.matchers.chain;

import org.hamcrest.StringDescription;
import org.junit.Test;
import static org.cthul.matchers.chain.XOrChainMatcher.either;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class XOrChainMatcherTest {

    @Test
    public void test_matching() {
        assertThat(1, is(either(greaterThan(1)).xor(lessThan(5))));
        assertThat(3, not(either(greaterThan(1)).xor(lessThan(5))));
    }
    
    @Test
    public void test_description() {
        String s = either(greaterThan(1)).xor(lessThan(5)).toString();
        assertThat(s, is("a value greater than <1> xor a value less than <5>"));
    }
    
    @Test
    public void test_mismatch_description() {
        StringDescription desc = new StringDescription();
        either(greaterThan(1)).xor(lessThan(5)).describeMismatch(3, desc);
        assertThat(desc.toString(), is("a value greater than <1> and a value less than <5>"));
        
        desc = new StringDescription();
        either(greaterThan(5)).xor(lessThan(1)).describeMismatch(3, desc);
        assertThat(desc.toString(), is("<3> was less than <5> and <3> was greater than <1>"));
    }
}
