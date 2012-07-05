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
    
    FormatConfiguration conf = FormatConfiguration.getDefault();
    
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
        Message msg = new Message(conf, "%1$d %<d %d %d", 1, 2);
        assertThat(msg.toString(), is("1 1 1 2"));
    }
    
    @Test
    public void testFormat2() {
        Message msg = new Message(conf, "%.$d %>d %<$d %d", 1, 2);
        assertThat(msg.toString(), is("1 2 2 2"));
    }
    
    @Test
    public void testFormat3() {
        Message msg = new Message(conf, "%1$d %`$d %d %d", 1, 2);
        assertThat(msg.toString(), is("1 1 1 2"));
    }
    
    @Test
    public void testFormat4() {
        Message msg = new Message(conf, "%$d %´d %`$d %d", 1, 2);
        assertThat(msg.toString(), is("1 2 2 2"));
    }
    
    @Test
    public void testConfiguration() {
        FormatConfiguration conf2 = new FormatConfiguration();
        ClassNameFormat.INSTANCE.register(conf2);
        Message msg = new Message(conf2, "%iC %<IC %<jClass", 1);
        assertThat(msg.toString(), is("java.lang.Integer JAVA.LANG.INTEGER java.lang.Integer"));
    }
    
    @Test
    public void test_noarg_format() {
        Proc format = Procs.invoke(Formatter.class, "Format", String.class, Object[].class);
        assertThat(format.call("%%"), returns("%"));
        assertThat(format.call("asd%%asd"), returns("asd%asd"));
        assertThat(format.call("%d%n%d", 1, 2), returns("1\n2"));
    }
}
