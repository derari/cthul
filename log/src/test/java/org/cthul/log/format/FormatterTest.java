package org.cthul.log.format;

import org.cthul.log.CLogConfiguration;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.CoreMatchers.*;

/**
 *
 * @author Arian Treffer
 */
public class FormatterTest {
    
    CLogConfiguration conf = CLogConfiguration.getDefault();
    
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
        CLogConfiguration conf2 = new CLogConfiguration();
        ClassName.register(conf2);
        Message msg = new Message(conf2, "%iC %<IC %<jClass", 1);
        assertThat(msg.toString(), is("java.lang.Integer JAVA.LANG.INTEGER java.lang.Integer"));
    }
}
