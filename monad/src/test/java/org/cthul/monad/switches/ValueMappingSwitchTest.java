package org.cthul.monad.switches;

import org.cthul.monad.switches.Switch;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class ValueMappingSwitchTest {

    @Test
    public void testMapValue() {
        String result = Switch.choose(10)
                .<String>mapResult()
                .ifTrue(i -> i < 100).set("<100")
                .ifTrue(i -> i < 50).set("<50")
                .orElse().set("default");
        assertThat(result, is("<100"));
    }
    
    @Test
    public void testMapValueAndResult() {
        String result = Switch.choose(10)
                .<String>mapResult()
                .mapResult(s -> s + " ...")
                .ifTrue(i -> i < 100).set("<100")
                .ifTrue(i -> i < 50).set("<50")
                .orElse().set("default");
        assertThat(result, is("<100 ..."));
    }
}
