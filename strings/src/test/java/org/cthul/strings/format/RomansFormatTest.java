package org.cthul.strings.format;

import org.cthul.strings.Formatter;
import org.junit.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

/**
 *
 * @author Arian Treffer
 */
public class RomansFormatTest {
    
    private FormatConfiguration conf;
    
    public RomansFormatTest() {
        conf = new FormatConfiguration();
        RomansFormat.INSTANCE.register(conf);
    }
    
    private String format(String s, Object... values) {
        return Formatter.Format(conf, null, s, values);
    }

    @Test
    public void test_format() {
        assertThat(format("%4ir %4Ir", 4, 19), is("  iv  XIX"));
        assertThat(format("%ir", 5951), is("mmmmmcmli"));
    }

}
