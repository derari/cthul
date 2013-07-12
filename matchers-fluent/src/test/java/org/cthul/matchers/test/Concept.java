package org.cthul.matchers.test;

import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import org.hamcrest.Matcher;
import static org.hamcrest.Matchers.*;
import static org.cthul.matchers.CoreFluents.*;

/**
 *
 */
public class Concept {
    
    @Test
    public void concept() {
        List<Integer> list = Arrays.asList(1, 3, 5);
        
        assertThat(1)
                .is(lessThan(3))
                .and(greaterThan(0));
        
        Matcher<Integer> m = match(Integer.class)
                .is(greaterThan(3))
                .or(lessThan(0));
        assertThat(1).isNot(m);
        
        assertThat(eachOf(list)).is(lessThan(10));
        
        assertThat(list)
                .isNot(empty())
                .and()._(eachInt()).is(lessThan(10))
                .and(hasItem(1));
        
//        Matcher<Iterable<Integer>> m2 = match(each(Integer.class));
        
    }
}
