package org.cthul.log;

import org.junit.*;

/**
 *
 * @author Arian Treffer
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
