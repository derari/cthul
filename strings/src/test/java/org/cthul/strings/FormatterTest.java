package org.cthul.strings;

import org.cthul.strings.format.ClassNameFormat;
import org.cthul.strings.format.FormatConfiguration;
import org.cthul.strings.format.Message;
import org.junit.*;
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
    public void testForma2t() {
        Message msg = new Message(conf, "%.$d %>d %<$d %d", 1, 2);
        assertThat(msg.toString(), is("1 2 2 2"));
    }
    
    @Test
    public void testConfiguration() {
        FormatConfiguration conf2 = new FormatConfiguration();
        ClassNameFormat.INSTANCE.register(conf2);
        Message msg = new Message(conf2, "%iC %<IC %<jClass", 1);
        assertThat(msg.toString(), is("java.lang.Integer JAVA.LANG.INTEGER java.lang.Integer"));
    }
}
