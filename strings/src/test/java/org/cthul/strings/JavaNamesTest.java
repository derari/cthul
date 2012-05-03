package org.cthul.strings;

import org.hamcrest.Matcher;
import org.junit.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.cthul.strings.JavaNames.*;

/**
 *
 * @author Arian Treffer
 */
public class JavaNamesTest {
    
    public JavaNamesTest() {
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

    private static Matcher<String[]> isArray(String... values) {
        return arrayContaining(values);
    }
    
    /**
     * Test of tokenize method, of class JavaNames.
     */
    @Test
    public void test_tokenize() {
        assertThat(tokenize("hello"), isArray("hello"));
        assertThat(tokenize("helloWorld"), isArray("hello", "World"));
        assertThat(tokenize("hello_World"), isArray("hello", "World"));
        assertThat(tokenize("HTTPRequest"), isArray("HTTP", "Request"));
        
        assertThat(tokenize("HE_wo"), isArray("HE", "wo"));
    }
    
    @Test
    public void test_camelCase() {
        assertThat(camelCase("Ab_CD_ef"), is("abCdEf"));
    }
    
    @Test
    public void test_CamelCase() {
        assertThat(CamelCase("Ab_CD_ef"), is("AbCdEf"));
    }
    
    @Test
    public void test_under_score() {
        assertThat(under_score("Ab_CD_ef"), is("ab_cd_ef"));
    }
    
    @Test
    public void test_UNDER_SCORE() {
        assertThat(UNDER_SCORE("Ab_CD_ef"), is("AB_CD_EF"));
    }
    
}
