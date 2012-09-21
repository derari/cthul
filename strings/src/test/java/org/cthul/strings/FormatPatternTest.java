package org.cthul.strings;

import java.util.List;
import java.util.regex.MatchResult;
import org.junit.Test;
import static org.cthul.matchers.CthulMatchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;


/**
 *
 * @author Arian Treffer
 */
public class FormatPatternTest {
    
    @Test
    public void test_match() {
        List<Object> result = FormatPattern.match("a%db%sc", "a123bfooc");
        assertThat(result.get(0), isInstanceOf(Integer.class).that(is(123)));
        assertThat(result.get(1), isInstanceOf(String.class).that(is("foo")));
    }
    
    @Test
    public void test_regex() {
        List<Object> result = FormatPattern.match("a%iRm/(\\d+).*/c%d", "a123bfooc4");
        assertThat(result.get(1), isInstanceOf(Integer.class).that(is(4)));
        MatchResult mr = (MatchResult) result.get(0);
        assertThat(mr.group(1), is("123"));
    }

}
