package org.cthul.strings;

import org.cthul.proc.Proc;
import org.cthul.proc.Procs;
import org.cthul.strings.format.*;
import org.junit.*;
import static org.cthul.matchers.CthulMatchers.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

/**
 *
 * @author Arian Treffer
 */
public class FormatterTest {
    
    static FormatConfiguration conf = FormatConfiguration.getDefault();
    static Proc format = Procs.invoke(Formatter.class, "Format", FormatConfiguration.class, String.class, Object[].class);
    static Proc format(FormatConfiguration c, String s, Object... args) {
        return format.call(c, s, args);
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
        assertThat(format(conf, "%1$d %<d %d %d", 1, 2), returns("1 1 1 2"));
    }
    
    @Test
    public void testFormat2() {
        assertThat(format(conf, "%.$d %>d %<$d %d", 1, 2), returns("1 2 2 2"));
    }
    
    @Test
    public void testFormat3() {
        assertThat(format(conf, "%1$d %`$d %d %d", 1, 2), returns("1 1 1 2"));
    }
    
    @Test
    public void testFormat4() {
        assertThat(format(conf, "%$d %´d %`$d %d", 1, 2), returns("1 2 2 2"));
    }
    
    @Test
    public void testConfiguration() {
        FormatConfiguration conf2 = new FormatConfiguration();
        ClassNameFormat.INSTANCE.register(conf2);
        assertThat(format(conf2, "%iC %<IC %<jClass", 1), returns("java.lang.Integer JAVA.LANG.INTEGER java.lang.Integer"));
    }
    
    @Test
    public void test_noarg_format() {
        assertThat(format(null, "%%"), returns("%"));
        assertThat(format(null, "asd%%asd"), returns("asd%asd"));
        assertThat(format(null, "%d%n%d", 1, 2), returns("1\n2"));
    }
}
