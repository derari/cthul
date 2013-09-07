package org.cthul.matchers;

import org.junit.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 *
 * @author Arian Treffer
 */
public class CthulMatchersTest {
    
    /**
     * Tests if the generated CthulMatchers is compiled.
     */
    @Test
    public void test_factory() {
        Object o = "";
        assertThat(o, is((Object) ""));
        assertThat(o, CthulMatchers.isA(String.class).that(is("")));
    }
    
}
