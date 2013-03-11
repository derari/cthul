package org.cthul.matchers.fluent.assertion;

import org.cthul.matchers.fluent.strategy.SingleItemStrategy;
import org.cthul.matchers.fluent.strategy.MatchingObject;
import org.junit.Test;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;

/**
 *
 * @author derari
 */
public class SingleItemStrategyTest {

    @Test
    public void test_validate_success() {
        SingleItemStrategy<String> sis = new SingleItemStrategy<>("hello");
        Object mismatch = sis.validate(equalTo("hello"));
        assertThat("No mismatch on success", mismatch, is(MatchingObject.VALID));
    }
    
    @Test
    public void test_validate_fail() {
        SingleItemStrategy<String> sis = new SingleItemStrategy<>("world");
        Object mismatch = sis.validate(equalTo("hello"));
        assertThat("Invalid object on failure", mismatch, is(equalTo((Object) "world")));
    }
}
