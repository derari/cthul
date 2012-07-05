package org.cthul.strings.format;

import java.io.IOException;
import org.cthul.proc.Proc;
import org.cthul.proc.Procs;
import org.cthul.strings.Formatter;
import org.junit.Test;
import static org.cthul.matchers.CthulMatchers.raisesException;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 *
 * @author Arian Treffer
 */
public class FormatAlignmentBaseTest {
    
    private FormatConfiguration conf;
    
    public FormatAlignmentBaseTest() {
        TestFormatAlignmentBase instance = new TestFormatAlignmentBase();
        conf = new FormatConfiguration();
        conf.setShortFormat('a', instance);
    }
    
    private Proc pFormat = Procs.invoke("format");
    
    private String format(String s, Object... values) {
        return Formatter.Format(conf, null, s, values);
    }
    
    @Test
    public void test_justify_left() {
        assertThat(format("x%-3iax",    1), is("x1  x"));
        assertThat(format("x%-3iax",   12), is("x12 x"));
        assertThat(format("x%-3iax",  123), is("x123x"));
        assertThat(format("x%-3iax", 1234), is("x1234x"));
    }

    @Test
    public void test_justify_right() {
        assertThat(format("x%3iax",    1), is("x  1x"));
        assertThat(format("x%3iax",   12), is("x 12x"));
        assertThat(format("x%3iax",  123), is("x123x"));
        assertThat(format("x%3iax", 1234), is("x1234x"));
    }

    @Test
    public void test_justify_center() {
        assertThat(format("x%|3iax",    1), is("x 1 x"));
        assertThat(format("x%|3iax",   12), is("x12 x"));
        assertThat(format("x%|3iax",  123), is("x123x"));
        assertThat(format("x%|3iax", 1234), is("x1234x"));
    }
    
    @Test
    public void test_justification_flag_error() {
        assertThat(pFormat.call("%-|3ia", 1), raisesException());
    }

    @Test
    public void test_padding() {
        assertThat(format("x%03iax",    1), is("x001x"));
        assertThat(format("x%_3iax",   12), is("x_12x"));
        assertThat(format("x%3iax",    12), is("x 12x"));
    }

    @Test
    public void test_padding_flag_error() {
        assertThat(pFormat.call("%0_3ia", 1), raisesException());
    }

    public class TestFormatAlignmentBase extends FormatAlignmentBase {

        @Override
        protected int format(Appendable a, Object value, java.util.Locale locale, String flags, int precision, String formatString, int position) throws IOException {
            a.append(String.valueOf(value));
            return 0;
        }
    }
}
