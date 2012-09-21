package org.cthul.strings.format.conversion;

import java.io.IOException;
import java.util.IllegalFormatFlagsException;
import java.util.Locale;
import org.cthul.proc.Proc;
import org.cthul.proc.Procs;
import org.cthul.strings.Formatter;
import org.cthul.strings.format.FormatterAPIStub;
import org.cthul.strings.format.conversion.FormatAlignmentBase;
import org.junit.Test;
import static org.cthul.matchers.CthulMatchers.raises;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 *
 * @author Arian Treffer
 */
public class FormatAlignmentBaseTest {
    
    TestFormatAlignmentBase instance = new TestFormatAlignmentBase();
    
    private Proc format = Procs.invoke(this, "format");
    
    private String format(Object value, String flags, int width) throws IOException {
        return FormatterAPIStub.format(instance, value, Locale.ENGLISH, flags, width, -1, "", 0);
    }
    
    @Test
    public void test_justify_left() throws IOException {
        assertThat(format(   1, "-", 3), is("1  "));
        assertThat(format(  12, "-", 3), is("12 "));
        assertThat(format( 123, "-", 3), is("123"));
        assertThat(format(1234, "-", 3), is("1234"));
    }

    @Test
    public void test_justify_right() throws IOException {
        assertThat(format(   1, "", 3), is("  1"));
        assertThat(format(  12, "", 3), is(" 12"));
        assertThat(format( 123, "", 3), is("123"));
        assertThat(format(1234, "", 3), is("1234"));
    }

    @Test
    public void test_justify_center() throws IOException {
        assertThat(format(   1, "|", 3), is(" 1 "));
        assertThat(format(  12, "|", 3), is("12 "));
        assertThat(format( 123, "|", 3), is("123"));
        assertThat(format(1234, "|", 3), is("1234"));
    }
    
    @Test
    public void test_justification_flag_error() {
        assertThat(format.call(1, "-|", 3), raises(IllegalFormatFlagsException.class));
    }

    @Test
    public void test_padding() throws IOException {
        assertThat(format(   1, "0", 3), is("001"));
        assertThat(format(   1, "_", 3), is("__1"));
        assertThat(format(   1,  "", 3), is("  1"));
    }

    @Test
    public void test_padding_flag_error() {
        assertThat(format.call(1, "_0", 3), raises(IllegalFormatFlagsException.class));
    }

    public class TestFormatAlignmentBase extends FormatAlignmentBase {

        @Override
        protected int format(Appendable a, Object value, Locale locale, String flags, int precision, String formatString, int position) throws IOException {
            a.append(String.valueOf(value));
            return 0;
        }
        
    }
}
