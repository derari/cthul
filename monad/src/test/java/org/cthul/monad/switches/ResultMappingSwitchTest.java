package org.cthul.monad.switches;

import org.cthul.monad.switches.Switch;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class ResultMappingSwitchTest {

    @Test
    public void testMatch1() {
        int result = Switch.choose(10)
                .ifTrue(i -> i < 100).set(1)
                .ifTrue(i -> i < 50).set(2)
                .orDefault();
        assertThat(result, is(1));
    }

    @Test
    public void testMatch2() {
        int result = Switch.choose(10)
                .ifTrue(i -> i < 5).set(1)
                .ifTrue(i -> i < 15).set(2)
                .orDefault();
        assertThat(result, is(2));
    }

    @Test
    public void testOrDefault() {
        int result = Switch.choose(20)
                .ifTrue(i -> i < 5).set(1)
                .ifTrue(i -> i < 15).set(2)
                .orDefault();
        assertThat(result, is(20));
    }
}
