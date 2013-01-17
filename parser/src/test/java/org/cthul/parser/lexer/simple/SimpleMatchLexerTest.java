package org.cthul.parser.lexer.simple;

import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import org.cthul.parser.api.*;
import org.cthul.parser.lexer.api.MatchEval;
import org.cthul.parser.token.DoubleToken;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.cthul.matchers.CthulMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 *
 * @author derari
 */
public class SimpleMatchLexerTest {
    
    public SimpleMatchLexerTest() {
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

    protected RuleKey rk(String s) {
        return new RuleKey(s, 0);
    }
    
    @Test
    public void test_scan() {
        SimpleMatchLexerBuilder<DoubleToken> lxb = new SimpleMatchLexerBuilder<>();
        Pattern dPattern = Pattern.compile("([\\d]+\\.[\\d]+);");
        lxb.addRegexToken(rk("D"), new ParseEval(), dPattern);
        lxb.addStringToken(rk("Pi"), new PiEval(), "Pi;");
        
        SimpleLexer<? extends TokenInput<DoubleToken>> lx = lxb.createLexer();
        TokenInput<DoubleToken> ti = lx.scan(Context.forString("1.0;Pi;2.5;"));
        double total = 0;
        for (DoubleToken dt: ti) {
            total += dt.eval();
        }
        assertThat(total, is(1.0+Math.PI+2.5));
    }
    
    protected class PiEval implements MatchEval<DoubleToken, MatchResult> {
        @Override
        public DoubleToken eval(Context<? extends StringInput> context, MatchResult match, int start, int end) {
            return new DoubleToken(0, "Pi", 0, Math.PI, 0, start, end, 0);
        }
    }
    
    protected class ParseEval implements MatchEval<DoubleToken, MatchResult> {
        @Override
        public DoubleToken eval(Context<? extends StringInput> context, MatchResult match, int start, int end) {
            double d = Double.parseDouble(match.group(1));
            return new DoubleToken(0, "D", 0, d, 0, start, end, 0);
        }
    }
}
