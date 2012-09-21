package org.cthul.strings.format.conversion;

import java.util.*;
import org.cthul.proc.Proc;
import org.cthul.strings.format.FormatterAPIStub;
import org.cthul.strings.format.conversion.RomansConversion;
import org.junit.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.cthul.matchers.CthulMatchers.*;

/**
 *
 * @author Arian Treffer
 */
public class RomansFormatTest {
    
    private String format(int value, String flags, int width, int precision) {
        return FormatterAPIStub.format(RomansConversion.INSTANCE, value, Locale.ENGLISH, flags, width, precision, "", 0);
    }
    
    private Proc formatCall(int value, String flags, int width, int precision) {
        return FormatterAPIStub.formatCall(RomansConversion.INSTANCE, value, Locale.ENGLISH, flags, width, precision, "", 0);
    }
    
    @Test
    public void test_simple() {
        assertThat(format(   0, null, -1, -1), is("n"));
        assertThat(format(   3, null, -1, -1), is("iii"));
        assertThat(format(5915, null, -1, -1), is("mmmmmcmxv"));
    }

    @Test
    public void test_width() {
        assertThat(format(6,  "_", 5, -1), is("___vi"));
    }
    
    @Test
    public void test_precision() {
        assertThat(format(4, null, -1, 0), is("iiii"));
        assertThat(format(4, null, -1, 1), is("iv"));
        assertThat(format(4, null, -1, 2), is("iv"));
        
        assertThat(format(999, null, -1, 0), is("dcccclxxxxviiii"));
        assertThat(format(999, null, -1, 1), is("cmxcix"));
        assertThat(format(999, null, -1, 2), is("im"));
        
        assertThat(formatCall(0, null, -1, 3), raises(IllegalFormatPrecisionException.class));
    }
    
}
