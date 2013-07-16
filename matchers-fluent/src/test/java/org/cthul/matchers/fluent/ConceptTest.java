package org.cthul.matchers.fluent;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.cthul.matchers.fluent.adapters.SimpleAdapter;
import org.cthul.matchers.fluent.value.MatchValue;
import org.cthul.matchers.fluent.value.MatchValueAdapter;
import org.junit.Test;
import org.hamcrest.Matcher;
import static org.hamcrest.Matchers.*;
import static org.cthul.matchers.CoreFluents.*;

/**
 *
 */
public class ConceptTest {
    
    @Test
    public void dsl() {
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
        
        assertThat(anyOf(list)).is(equalTo(3));  
        
        assertThat(sizeOf(list)).is(equalTo(3));  
        
        assertThat(list)
                .isNot(empty())
                .and(size()).is(lessThan(5))
                .and(eachInt()).is(lessThan(10))
                .andNot(hasItem(7));
    }
    
    public static MatchValue<Integer> sizeOf(Collection<?> c) {
        return size().adapt(c);
    }
    
    public static MatchValueAdapter<Collection<?>, Integer> size() {
        return new SimpleAdapter<Collection<?>, Integer>("size") {
            @Override
            protected Integer getValue(Collection<?> v) {
                return v.size();
            }
        };
    }
}
