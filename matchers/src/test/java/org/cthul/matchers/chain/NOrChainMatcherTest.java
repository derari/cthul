package org.cthul.matchers.chain;

import org.hamcrest.StringDescription;
import org.junit.Test;
import static org.cthul.matchers.chain.NOrChainMatcher.neither;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class NOrChainMatcherTest {

    @Test
    public void test_matching() {
        assertThat(3, is(neither(greaterThan(5)).nor(lessThan(1))));
        assertThat(6, not(neither(greaterThan(5)).nor(lessThan(1))));
    }
    
    @Test
    public void test_description() {
        String s = neither(greaterThan(5)).nor(lessThan(1)).toString();
        assertThat(s, is("neither a value greater than <5> nor a value less than <1>"));
    }
    
    @Test
    public void test_mismatch_description() {
        StringDescription desc = new StringDescription();
        neither(greaterThan(5)).nor(lessThan(1)).describeMismatch(6, desc);
        assertThat(desc.toString(), is("a value greater than <5>"));
        
        desc = new StringDescription();
        neither(greaterThan(5)).nor(lessThan(1)).describeMismatch(0, desc);
        assertThat(desc.toString(), is("a value less than <1>"));
    }
}
