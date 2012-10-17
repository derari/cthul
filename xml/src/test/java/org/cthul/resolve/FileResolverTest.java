package org.cthul.resolve;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 *
 * @author Arian Treffer
 */
public class FileResolverTest {
    
    FileResolver instance;
    
    public FileResolverTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        instance = (FileResolver) new FileResolver("src/test/resources")
                .addDomain("http://example.org", "$1");
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void test_get_directory() {
        RRequest req = new RRequest(null, null, null, null);
        RResult res = instance.get(req, "schema");
        assertThat(res, is(nullValue()));
    }

    @Test
    public void test_get_file() {
        RRequest req = new RRequest(null, null, null, null);
        RResult res = instance.get(req, "schema/a.xsd");
        assertThat(res.getInputStream(), is(notNullValue()));
    }

}
