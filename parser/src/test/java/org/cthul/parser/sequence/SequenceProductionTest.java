package org.cthul.parser.sequence;

import java.util.Iterator;
import java.util.List;
import java.util.regex.MatchResult;
import org.cthul.parser.AmbiguousParser;
import org.cthul.parser.ParserBuilder;
import org.cthul.parser.api.Context;
import org.cthul.parser.api.RuleKey;
import org.cthul.parser.api.StringInput;
import org.cthul.parser.grammar.earleyx.EarleyXItGrammarBuilder;
import org.cthul.parser.lexer.api.PlainStringInputEval;
import org.cthul.parser.lexer.lazy.LazyLexerBuilder;
import org.hamcrest.Matcher;
import org.junit.*;
import static org.cthul.parser.util.Tools.keys;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 *
 */
public class SequenceProductionTest {
    
    ParserBuilder<String, MatchResult> pb = new ParserBuilder<>(
            new LazyLexerBuilder<String>(), 
            new EarleyXItGrammarBuilder<StringInput>(),
            PlainStringInputEval.instance());
    RuleKey item = pb.stringToken("1");
    RuleKey sep = pb.stringToken(",");

    
    public SequenceProductionTest() {
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

    protected ArrayListBuilder seq() {
        return new ArrayListBuilder();
    }
    
    @Test
    public void test_simple_list() {        
        SequenceProductionBuilder sp = new SequenceProductionBuilder(seq(), keys(item), keys(sep), false, false, false, 1, 1);
        sp.buildLeftDeep(pb.productionBuilder("list", 0));
        
        AmbiguousParser<List<?>> parser = pb.createParser(AmbiguousParser.<List<?>>factory());
        
        Iterator<List<?>> it = parser.parse(Context.forString("1,1,1"), "list").iterator();
        assertThat(it.next(), (Matcher) contains("1", "1", "1"));
        assertThat(it.hasNext(), is(false));
    }
    
    @Test
    public void test_unflattend_list() {
        SequenceProductionBuilder sp = new SequenceProductionBuilder(seq(), keys(item, item), keys(sep), false, false, false, 1, 1);
        sp.buildLeftDeep(pb.productionBuilder("list", 0));
        
        AmbiguousParser<List<?>> parser = pb.createParser(AmbiguousParser.<List<?>>factory());
        
        List<?> list = parser.parse(Context.forString("11,11"), "list").iterator().next();
        assertThat(list, (Matcher) contains(new Object[]{"1", "1"}, new Object[]{"1", "1"}));
    }

    @Test
    public void test_flattend_list() {
        SequenceProductionBuilder sp = new SequenceProductionBuilder(seq(), keys(item, item), keys(sep), true, false, false, 1, 1);
        sp.buildLeftDeep(pb.productionBuilder("list", 0));
        
        AmbiguousParser<List<?>> parser = pb.createParser(AmbiguousParser.<List<?>>factory());
        
        List<?> list = parser.parse(Context.forString("11,11"), "list").iterator().next();
        assertThat(list, (Matcher) contains("1", "1", "1", "1"));
    }
    
    @Test
    public void test_empty_list() {        
        SequenceProductionBuilder sp = new SequenceProductionBuilder(seq(), keys(item), keys(sep), false, false, true, 1, 1);
        sp.buildLeftDeep(pb.productionBuilder("list", 0));
        
        AmbiguousParser<List<?>> parser = pb.createParser(AmbiguousParser.<List<?>>factory());
        
        Iterator<List<?>> it = parser.parse(Context.forString(""), "list").iterator();
        assertThat(it.next(), (Matcher) is(empty()));
        assertThat(it.hasNext(), is(false));
        
        it = parser.parse(Context.forString("1"), "list").iterator();
        assertThat(it.next(), (Matcher) contains("1"));
        assertThat(it.hasNext(), is(false));
        
        it = parser.parse(Context.forString("1,1"), "list").iterator();
        assertThat(it.next(), (Matcher) contains("1", "1"));
        assertThat(it.hasNext(), is(false));

    }
    

}
