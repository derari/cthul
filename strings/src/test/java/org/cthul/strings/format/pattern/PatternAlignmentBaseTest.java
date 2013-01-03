package org.cthul.strings.format.pattern;

import java.util.Locale;
import java.util.regex.Matcher;
import org.cthul.proc.Proc;
import org.cthul.strings.format.PatternAPI;
import org.cthul.strings.format.PatternAPIStub;
import org.junit.Test;
import static org.hamcrest.Matchers.*;
import static org.cthul.matchers.CthulMatchers.*;
import static org.hamcrest.MatcherAssert.*;

/**
 *
 * @author Arian Treffer
 */
public class PatternAlignmentBaseTest {
    
    PatternAlignmentBaseImpl instance = new PatternAlignmentBaseImpl();
    
    private Object parse(String input, String flags, int width) {
        instance.exact = false;
        return PatternAPIStub.parse(instance, input, Locale.ENGLISH, flags, width, -1, "", 0);
    }
    
    private Object parseExact(String input, String flags, int width) {
        instance.exact = true;
        return PatternAPIStub.parse(instance, input, Locale.ENGLISH, flags, width, -1, "", 0);
    }
    
    private Proc parseCall(String input, String flags, int width) {
        instance.exact = false;
        return PatternAPIStub.parseCall(instance, input, Locale.ENGLISH, flags, width, -1, "", 0);
    }
    
    private Proc parseExactCall(String input, String flags, int width) {
        instance.exact = true;
        return PatternAPIStub.parseCall(instance, input, Locale.ENGLISH, flags, width, -1, "", 0);
    }
    
    public PatternAlignmentBaseTest() {
    }
    
    @Test
    public void test_simple_regex() {
        String s = (String) parse("xy", null, -1);
        assertThat(s, is("any/xy"));
    }
    
    @Test
    public void test_simple_align_right() {
        String s = (String) parse("  xy", null, 5);
        assertThat(s, is("any/xy"));
    }

    @Test
    public void test_simple_align_left() {
        String s = (String) parse("xy00", "0-", 5);
        assertThat(s, is("any/xy"));
    }
    
    @Test
    public void test_simple_align_padding_match() {
        assertThat(parseCall("  xy", "0", 5), raises("no match"));
        assertThat(parseCall("00xy", "0", 5), returns());
    }

    @Test
    public void test_simple_align_center() {
        String s = (String) parse("__xy_", "|_", 5);
        assertThat(s, is("any/xy"));
    }
    
    @Test
    public void test_exact_align_right() {
        assertThat(parseExactCall( "  ab", null, 4), returns("exact2/ab"));
        assertThat(parseExactCall( "abcd", null, 4), returns("exact4/abcd"));
        assertThat(parseExactCall("xabcy", null, 4), returns("exact-1/xabcy"));
        assertThat(parseExactCall(  " ab", null, 4), raises("no match"));
        assertThat(parseExactCall("   ab", null, 4), raises("no match"));
    }
    
    @Test
    public void test_exact_no_empty_match() {
        assertThat(parseExactCall("0000", "0", 4), raises("no match"));
    }

    public class PatternAlignmentBaseImpl extends PatternAlignmentBase {
        
        public boolean exact = false;

        @Override
        protected boolean matchExactWidth(String flags, int width) {
            return exact;
        }

        @Override
        public int toRegex(PatternAPI regex, Locale locale, String flags, char padding, int precision, String formatString, int position) {
            regex.putMemento("any");
            regex.append("x(abc)?y");
            regex.addedCapturingGroup();
            return 0;
        }

        @Override
        protected int toRegex(PatternAPI regex, Locale locale, String flags, char padding, int width, int precision, String formatString, int position) {
            regex.putMemento("exact"+width);
            if (width < 0) {
                regex.append("x(abc)y");
                regex.addedCapturingGroup();
            } else if (width == 0) {
                return IGNORE_SUBPATTERN;
            } else {
                for (int i = 0; i < width; i++) {
                    char c = (char) ('a' + i);
                    regex.append("(" + c + ")");
                }
                regex.addedCapturingGroups(width);
            }
            return 0;
        }

        @Override
        public Object parse(Matcher matcher, int capturingBase, int width, Object memento, Object lastArgValue) {
            return String.valueOf(memento) + "/" + matcher.group(capturingBase);
        }
    }
    
}
