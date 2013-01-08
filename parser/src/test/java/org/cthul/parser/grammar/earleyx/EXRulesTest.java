package org.cthul.parser.grammar.earleyx;

import java.util.List;
import org.cthul.parser.AmbiguousParser;
import org.cthul.parser.ParserBuilder;
import org.cthul.parser.api.Context;
import org.cthul.parser.api.RuleKey;
import org.cthul.parser.api.StringInput;
import org.cthul.parser.lexer.api.PlainStringInputEval;
import org.cthul.parser.lexer.lazy.LazyLexerBuilder;
import org.hamcrest.Matcher;
import org.junit.*;
import static org.cthul.parser.util.Tools.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

public class EXRulesTest {
    
    public EXRulesTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void testCreateListRule() {
        ParserBuilder<String> pb = new ParserBuilder<>(new LazyLexerBuilder<String>(), new EarleyXItGrammarBuilder<StringInput>());
        RuleKey item = pb.addStringToken(PlainStringInputEval.instance(), "1");
        RuleKey sep = pb.addStringToken(PlainStringInputEval.instance(), ",");
        EXRules.createListRule(pb.productionBuilder("list", 0), keys(item), keys(sep), true);
        AmbiguousParser<List<?>> parser = pb.createParser(AmbiguousParser.<List<?>>factory());
        
        List<?> list = parser.parse(Context.forString("1,1,1"), "list").iterator().next();
        assertThat(list, (Matcher) contains("1", "1", "1"));
    }

}
