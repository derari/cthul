package org.cthul.strings;

import java.util.UnknownFormatConversionException;
import org.cthul.proc.Proc;
import org.cthul.proc.Procs;
import org.cthul.strings.format.FormatStringParserTest;
import org.cthul.strings.format.FormatterConfiguration;
import org.cthul.strings.format.conversion.ClassNameConversion;
import org.junit.*;
import static org.cthul.matchers.CthulMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 *
 * @author Arian Treffer
 * @see FormatStringParserTest
 */
public class FormatterTest {
    
    static Proc format = Procs.invoke(Strings.class, "format", FormatterConfiguration.class, String.class, Object[].class);

    static Proc format(FormatterConfiguration c, String s, Object... args) {
        return format.call(c, s, args);
    }
    
    static Proc format(String s, Object... args) {
        return format.call(null, s, args);
    }
    
    public FormatterTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void testFormat() {
        assertThat(format("%d %ir %JAlpha", 1, 2, 3), returns("1 ii D"));
    }
    
    @Test
    public void testConfiguration() {
        FormatterConfiguration conf2 = new FormatterConfiguration(null);
        ClassNameConversion.INSTANCE.register(conf2);
        assertThat(format(conf2, "%iC", 1), returns("java.lang.Integer"));
        assertThat(format(conf2, "%ir", 1), raises(UnknownFormatConversionException.class));
    }
    
}
