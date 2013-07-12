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
        
        Matcher<Integer> between_5_and_10 = match(Integer.class)
                .is(lessThan(10))
                .and(greaterThan(5));
        assertThat(1).isNot(between_5_and_10);
        
        assertThat(eachOf(list)).is(lessThan(10));
        
        assertThat(list)
                .isNot(empty())
                .and(eachInt()).is(lessThan(10))
                .and(hasItem(1));
        
        Matcher<Iterable<? extends Integer>> each_is_gt_1 = match(eachInt())
                .is(greaterThan(1));
        assertThat(list).not(each_is_gt_1);
        
    }
}
