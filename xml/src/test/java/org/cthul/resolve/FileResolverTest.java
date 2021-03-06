package org.cthul.resolve;

import java.io.File;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 *
 * @author Arian Treffer
 */
public class FileResolverTest {
    
    FileResolver instance;
    
    @Before
    public void setUp() {
        instance = (FileResolver) new FileResolver("src/test/resources")
                .addDomain("http://example.org");
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void test_get_directory() {
        RRequest req = new RRequest(null, null, null, null);
        RResponse res = instance.get(req, "schema");
        assertThat(res != null && res.hasResult(), is(false));
    }

    @Test
    public void test_get_file() {
        RRequest req = new RRequest(null, null, null, null);
        RResult res = instance.get(req, "schema/a.xsd").getResult();
        assertThat(res.getInputStream(), is(notNullValue()));
    }

    @Test
    public void test_checked_base_dir() {
        File checkedBase = new File("src/test/resources/schema");
        File base = new File(checkedBase, "foo");
        instance = new FileResolver(base, checkedBase);
        RRequest req = new RRequest(null, null, null, null);
        
        // escapes base dir, but is in checked range
        RResult res = instance.get(req, "../a.xsd").getResult();
        assertThat(res, is(notNullValue()));
        assertThat(res.getInputStream(), is(notNullValue()));
        
        // escapes checked dir
        RResponse response = instance.get(req, "../../a.xml");
        assertThat(response != null && response.hasResult(), is(false));
        
        // try again with unchecked resolver
        instance = new FileResolver(base, (File) null);        
        res = instance.get(req, "../../a.xml").getResult();
        assertThat(res, is(notNullValue()));
    }
}
