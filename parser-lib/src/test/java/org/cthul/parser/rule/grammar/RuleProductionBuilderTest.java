package org.cthul.parser.rule.grammar;

import org.cthul.parser.AmbiguousParser;
import org.cthul.parser.rule.grammar.RuleProductionBuilder;
import java.util.ArrayList;
import java.util.List;
import org.cthul.parser.*;
import org.cthul.parser.api.Context;
import org.cthul.parser.api.Match;
import org.cthul.parser.grammar.api.ProductionMatch;
import org.cthul.parser.grammar.api.RuleEval;
import org.cthul.parser.grammar.earleyx.EarleyXItGrammarBuilder;
import org.cthul.parser.lexer.api.PlainStringInputEval;
import org.cthul.parser.lexer.lazy.LazyLexerBuilder;
import org.cthul.parser.lexer.simple.SimpleMatchLexerBuilder;
import org.cthul.parser.rule.AntiMatchRule;
import org.cthul.parser.rule.KeyMatchRule;
import org.cthul.parser.rule.LookAheadRule;
import org.cthul.parser.rule.NoResultRule;
import org.cthul.parser.rule.ProductionRule;
import org.cthul.parser.rule.Rule;
import org.cthul.parser.rule.grammar.RuleProductionBuilder.RuleProductionEval;
import org.cthul.parser.token.Token;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

public class RuleProductionBuilderTest {
    
    protected ParserBuilder<Object, Object> builder;
    
    protected Rule keyMatch(String key) {
        return (Rule) (Object) new KeyMatchRule(key, 0);
    }
    
    public RuleProductionBuilderTest() {
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

    @Test
    public void test_key_match() {
        Rule rule = keyMatch("a");
        new TestRPB().buildRule(rule, builder.productionBuilder("rule", 0));
        AmbiguousParser<List<String>> p = (AmbiguousParser) builder.createParser(AmbiguousParser.factory());
        List<String> result = p.parse(Context.forString("a"), "rule").iterator().next();
        assertThat(result, contains("a"));
    }
    
    @Test
    public void test_simple_production() {
        Rule rule = new ProductionRule(keyMatch("a"), keyMatch("b"), keyMatch("c"));
        new TestRPB().buildRule(rule, builder.productionBuilder("rule", 0));
        AmbiguousParser<List<String>> p = (AmbiguousParser) builder.createParser(AmbiguousParser.factory());
        List<String> result = p.parse(Context.forString("abc"), "rule").iterator().next();
        assertThat(result, contains("a", "b", "c"));
    }
    
    @Test
    public void test_nested_production() {
        Rule rule0 = new ProductionRule(keyMatch("b"), keyMatch("c"));
        Rule rule = new ProductionRule(keyMatch("a"), rule0);
        new TestRPB().buildRule(rule, builder.productionBuilder("rule", 0));
        AmbiguousParser<List<String>> p = (AmbiguousParser) builder.createParser(AmbiguousParser.factory());
        List<String> result = p.parse(Context.forString("abc"), "rule").iterator().next();
        assertThat(result, contains("a", "b", "c"));
    }
    
    @Test
    public void test_look_ahead() {
        Rule rule0 = new LookAheadRule(keyMatch("b"), keyMatch("c"));
        Rule rule = new ProductionRule(keyMatch("a"), rule0, keyMatch("b"), keyMatch("c"));
        new TestRPB().buildRule(rule, builder.productionBuilder("rule", 0));
        AmbiguousParser<List<String>> p = (AmbiguousParser) builder.createParser(AmbiguousParser.factory());
        List<String> result = p.parse(Context.forString("abc"), "rule").iterator().next();
        assertThat(result, contains("a", "b", "c", "b", "c"));
    }
    
    @Test
    public void test_anti_match() {
        Rule rule0 = new AntiMatchRule(keyMatch("b"), keyMatch("a"));
        Rule rule = new ProductionRule(keyMatch("a"), rule0, keyMatch("b"), keyMatch("c"));
        new TestRPB().buildRule(rule, builder.productionBuilder("rule", 0));
        AmbiguousParser<List<String>> p = (AmbiguousParser) builder.createParser(AmbiguousParser.factory());
        List<String> result = p.parse(Context.forString("abc"), "rule").iterator().next();
        assertThat(result, contains("a", "b", "c"));
    }
    
    @Test
    public void test_no_result() {
        Rule rule0 = new NoResultRule(keyMatch("b"));
        Rule rule = new ProductionRule(keyMatch("a"), rule0, keyMatch("c"));
        new TestRPB().buildRule(rule, builder.productionBuilder("rule", 0));
        AmbiguousParser<List<String>> p = (AmbiguousParser) builder.createParser(AmbiguousParser.factory());
        List<String> result = p.parse(Context.forString("abc"), "rule").iterator().next();
        assertThat(result, contains("a", "c"));
    }
    
    public static RuleEval lastEval = null;
    
    protected class TestRPB extends RuleProductionBuilder {

        @Override
        protected RuleEval baseEval(final RuleProductionEval root) {
            return lastEval = new RuleEval() {
                @Override
                public Object eval(Context<?> context, ProductionMatch match, Object arg) {
                    List<String> result = new ArrayList<>();
                    for (Match<?> m: root.eval(context, match, null)) {
                        result.add(m.getSymbol());
                    }
                    return result;
                }
            };
        }
        
    }
}
