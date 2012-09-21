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
    
    
    private StringBuilder sb;
    
    @Before
    public void setUp() {
        sb = new StringBuilder();
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
        String[] tokens = {"Ab", "CD", "ef"};
        camelCase(tokens, sb, true);
        assertThat(sb.toString(), is("abCdEf"));
    }
    
    @Test
    public void test_CamelCase() {
        String[] tokens = {"Ab", "CD", "ef"};
        camelCase(tokens, sb, false);
        assertThat(sb.toString(), is("AbCdEf"));
    }
    
    @Test
    public void test_under_score() {
        String[] tokens = {"Ab", "CD", "ef"};
        under_score(tokens, sb);
        assertThat(sb.toString(), is("ab_cd_ef"));
    }
    
    @Test
    public void test_UNDER_SCORE() {
        String[] tokens = {"Ab", "CD", "ef"};
        UNDER_SCORE(tokens, sb);
        assertThat(sb.toString(), is("AB_CD_EF"));
    }
    
}
