package org.cthul.log;

import java.util.Locale;
import org.junit.*;

/**
 *
 * @author derari
 */
public class CLocLogConfigurationTest {
    
    public CLocLogConfigurationTest() {
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
    public void testDetectClass() {
        CLocLogger<Enum<?>> log = CLocLogConfiguration.getDefault().forLocale(Locale.UK).getClassLogger();
        log.error(TestMessage.HELLO_WORLD, false);
    }
}
