package org.cthul.parser.annotation;

import org.cthul.parser.AmbiguousParser;
import org.cthul.parser.ParserBuilder;
import org.cthul.parser.annotation.scan.AnnotationScanner;
import org.cthul.parser.api.Context;
import org.cthul.parser.grammar.earleyx.EarleyXItGrammarBuilder;
import org.cthul.parser.lexer.api.PlainStringInputEval;
import org.cthul.parser.lexer.lazy.LazyLexerBuilder;
import org.cthul.parser.rule.KeyMatchRule;
import org.cthul.parser.rule.Rule;
import org.junit.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;


public class ProductionReaderTest {
    
    protected ParserBuilder<Object, Object> builder;
    
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
        builder = new ParserBuilder<>(new LazyLexerBuilder<>(), new EarleyXItGrammarBuilder(), null);
        for (String s: new String[]{"a","b","c"}) {
            builder.addStringToken(s, PlainStringInputEval.instance(), s);
        }
    }

    @After
    public void tearDown() {
    }
    
    public static class SimpleRule {
        
        private char sep;

        public SimpleRule() {
            sep = ' ';
        }

        public SimpleRule(char sep) {
            this.sep = sep;
        }
        
        @Production("a b")
        public String string(String a, String b) {
            return a + sep + b;
        }
        
    }
    
    @Test
    public void test_instance_method_by_class() {
        AnnotationScanner.apply(builder, SimpleRule.class);
        AmbiguousParser<String> p = (AmbiguousParser) builder.createParser(AmbiguousParser.factory());
        String result = p.parse(Context.forString("ab"), "string").iterator().next();
        assertThat(result, is("a b"));
    }
    
    @Test
    public void test_instance_method_by_impl() {
        AnnotationScanner.apply(builder, new SimpleRule('_'));
        AmbiguousParser<String> p = (AmbiguousParser) builder.createParser(AmbiguousParser.factory());
        String result = p.parse(Context.forString("ab"), "string").iterator().next();
        assertThat(result, is("a_b"));
    }
    
    public static class KeySelectRule {
        
        @Production({"a b", "b a"})
        public String string(@Step("a") String a, @Step("b") String b) {
            return a + "_" + b;
        }
    }

    @Test
    public void test_key_select() {
        AnnotationScanner.apply(builder, KeySelectRule.class);
        AmbiguousParser<String> p = (AmbiguousParser) builder.createParser(AmbiguousParser.factory());
        String result = p.parse(Context.forString("ba"), "string").iterator().next();
        assertThat(result, is("a_b"));
    }
    
    public static class IndexSelectRule {
        
        @Key("letter")
        @Production({"a","b","c"})
        public String l(String s) { return s; }
        
        @Production("letter letter letter")
        public String string(@Step(i=1) String a, String b) {
            return a + "_" + b;
        }
    }
    
    @Test
    public void test_index_select() {
        AnnotationScanner.apply(builder, IndexSelectRule.class);
        AmbiguousParser<String> p = (AmbiguousParser) builder.createParser(AmbiguousParser.factory());
        String result = p.parse(Context.forString("abc"), "string").iterator().next();
        assertThat(result, is("b_c"));
    }
    
}
