package org.cthul.log;

import java.util.Locale;
import org.junit.*;
import org.slf4j.Logger;
import static org.junit.Assert.*;

/**
 *
 * @author derari
 */
public class CLogConfigurationTest {
    
    public CLogConfigurationTest() {
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
        CLogger log = CLogConfiguration.getDefault().getClassLogger();
        log.error("Hello CLogConfigurationTest");
    }
}
