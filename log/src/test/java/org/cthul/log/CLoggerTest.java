package org.cthul.log;

import org.junit.*;

/**
 *
 * @author Arian Treffer
 */
public class CLoggerTest {
    
    public CLoggerTest() {
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
    public void testFix() {
        CLogger log = CLoggerFactory.getClasslogger();
        log.error("hello world");
    }
}
