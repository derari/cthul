package org.cthul.monad.switches;

import org.cthul.monad.Scope;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class BasicResultSwitchTest {
    
    private final Scope scope = () -> "test scope";
    
    @Test
    public void testNestedChoice() {
        int result = scope.value(100)
                .chooseByException()
                    .ifInstanceOf(RuntimeException.class).setValue(-1)
                    .orDefault()
                .chooseByValue()
                    .ifTrue(i -> i < 50).mapIfPresent(n -> n + 10)
                    .orElse()
                        .chooseByStatus()
                            .ifOk().setValue(17)
                            .orElse().map(n -> n) // todo: identity interface
                        .endUnchecked()
                .get();
        
        assertThat(result, is(17));
    }
    
}
