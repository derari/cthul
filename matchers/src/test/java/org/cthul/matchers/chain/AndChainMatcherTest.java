package org.cthul.matchers.chain;

import org.cthul.matchers.diagnose.QuickDiagnosingMatcher;
import org.cthul.matchers.diagnose.result.MatchResult;
import org.hamcrest.StringDescription;
import org.junit.Test;
import static org.cthul.matchers.chain.AndChainMatcher.both;
import static org.cthul.matchers.chain.OrChainMatcher.either;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class AndChainMatcherTest {

    @Test
    public void test_matching() {
        assertThat(3, is(both(greaterThan(1)).and(lessThan(5))));
        assertThat(1, not(both(greaterThan(1)).and(lessThan(5))));
    }
    
    @Test
    public void test_description() {
        String s = both(greaterThan(1)).and(lessThan(5)).toString();
        assertThat(s, is("a value greater than <1> and a value less than <5>"));
    }
    
    @Test
    public void test_mismatch_description() {
        StringDescription desc = new StringDescription();
        both(greaterThan(1)).and(lessThan(5)).describeMismatch(1, desc);
        assertThat(desc.toString(), is("<1> was equal to <1>"));
        
        desc = new StringDescription();
        both(greaterThan(1)).and(lessThan(5)).describeMismatch(6, desc);
        assertThat(desc.toString(), is("<6> was greater than <5>"));
    }
    
    @Test
    public void test_match_description() {
        QuickDiagnosingMatcher<Integer> m = both(either(lessThan(3)).or(greaterThan(5)));
        MatchResult<Integer> mr = m.matchResult(6);
        assertThat(mr.toString(),
                    is("a value greater than <5>"));
    }
}
