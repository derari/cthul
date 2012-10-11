package org.cthul.log;

import java.util.Locale;
import org.junit.*;

/**
 *
 * @author Arian Treffer
 */
public class CLocLoggerTest {
    
    public CLocLoggerTest() {
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
    public void testMessage() {
        CLocLogger<Enum<?>> log = CLocLogConfiguration.getDefault().forLocale(Locale.UK).getClassLogger();
        log.error(TestMessage.HELLO_WORLD, "everyone");
    }
}
