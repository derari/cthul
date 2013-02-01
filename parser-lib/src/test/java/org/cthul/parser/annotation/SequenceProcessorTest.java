package org.cthul.parser.annotation;

import java.util.List;
import java.util.regex.MatchResult;
import org.cthul.parser.AmbiguousParser;
import org.cthul.parser.annotation.scan.AnnotationScanner;
import org.cthul.parser.lexer.api.PlainStringInputEval;
import org.cthul.parser.rule.KeyMatchRule;
import org.cthul.parser.rule.Rule;
import org.cthul.parser.util.Format;
import org.junit.*;
import static org.cthul.matchers.CthulMatchers.matchesPattern;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;


public class SequenceProcessorTest {
    
    protected AmbiguousParser.Builder<Object, MatchResult> builder;
    
    protected Rule keyMatch(String key) {
        return (Rule) (Object) new KeyMatchRule(key, 0);
    }
    
    @BeforeClass
    public static void setUpClass() {

    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        builder = AmbiguousParser.buildEarleyWithoutLexer();
        for (String s: new String[]{"a",",","c"}) {
            builder.addStringToken(s, PlainStringInputEval.instance(), s);
        }
    }

    
    public static class SimpleList {
        
        @Sequence(item="a")
        public String string(List<String> values) {
            return Format.join("", ";", "", values);
        }
        
    }
    
    @Test
    public void test_simple_list() {
        AnnotationScanner.apply(builder, SimpleList.class);
        AmbiguousParser<String> p = builder.createParser();
        String result = p.parse("aaa", "string").iterator().next();
        assertThat(result, is("a;a;a"));
    }

    public static class UnflattenedList {
        
        @Sequence(item="a a")
        public String string(List<Object[]> values) {
            return Format.join("", ";", "", values);
        }
        
    }
    
    @Test
    public void test_unflattened_list() {
        AnnotationScanner.apply(builder, UnflattenedList.class);
        AmbiguousParser<String> p = builder.createParser();
        String result = p.parse("aaaa", "string").iterator().next();
        assertThat(result, matchesPattern("\\[L.*;\\[L.*"));
    }
    
    public static class FlattenedList {
        
        @Sequence(item="a a", flatten=true)
        public String string(List<String> values) {
            return Format.join("", ";", "", values);
        }
        
    }
    
    @Test
    public void test_flattened_list() {
        AnnotationScanner.apply(builder, FlattenedList.class);
        AmbiguousParser<String> p = builder.createParser();
        String result = p.parse("aaaa", "string").iterator().next();
        assertThat(result, matchesPattern("a;a;a;a"));
    }
    
    public static class ListWithSeparator {
        
        @Sequence(item="a", separator="`,`")
        public String string(List<String> values) {
            return Format.join("", ";", "", values);
        }
        
    }
    
    @Test
    public void test_list_with_separator() {
        AnnotationScanner.apply(builder, ListWithSeparator.class);
        AmbiguousParser<String> p = builder.createParser();
        String result = p.parse("a,a,a", "string").iterator().next();
        assertThat(result, matchesPattern("a;a;a"));
    }
    
}
