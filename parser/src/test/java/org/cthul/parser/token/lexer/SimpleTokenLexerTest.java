package org.cthul.parser.token.lexer;

import java.util.regex.Pattern;
import org.cthul.parser.api.Context;
import org.cthul.parser.api.RuleKey;
import org.cthul.parser.api.StringInput;
import org.cthul.parser.lexer.api.MatchEval;
import org.cthul.parser.token.*;
import org.junit.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class SimpleTokenLexerTest {
    
    public SimpleTokenLexerTest() {
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
    public void testSomeMethod() {
        SimpleTokenLexerBuilder<Number, Token<? extends Number>> lxb = new SimpleTokenLexerBuilder<>();
        Pattern dPattern = Pattern.compile("([\\d]+\\.[\\d]+);");
        lxb.addRegexToken(rk("D"), e(new ParseEval()), dPattern);
        lxb.addStringToken(rk("Pi"), e(new PiEval()), "Pi;");
        
        SimpleTokenLexer<Token<? extends Number>> lx = lxb.createLexer();
        TokenStream<Token<? extends Number>> ti = lx.scan(Context.forString("1.0;Pi;2.5;"));
        double total = 0;
        for (Token<? extends Number> token: ti) {
            total += token.eval().doubleValue();
        }
        assertThat(total, is(1.0+Math.PI+2.5));
    }
    
    protected <V, T extends Token<? extends V>> MatchEval<T, Object> e(
                    MatchEval<T, ? super TokenBuilder<V, ? super T>> eval) {
        return new TokenStreamInputEval<>(eval);
    }
    
    /**
     * Creates {@code Token<Double>}, which has to fit into a {@code Token<? extends Number>} token stream
     */
    protected class PiEval<T extends Token<?>> implements MatchEval<T, TokenBuilder<? super Double, ? extends T>> {
        @Override
        public T eval(Context<? extends StringInput> context, TokenBuilder<? super Double, ? extends T> tb, int start, int end) {
            return tb.newToken("Pi", Math.PI);
        }
    }
    
    protected class ParseEval implements MatchEval<DoubleToken, TokenBuilder<? super Double, ? super DoubleToken>> {
        @Override
        public DoubleToken eval(Context<? extends StringInput> context, TokenBuilder<? super Double, ? super DoubleToken> tb, int start, int end) {
            double d = Double.parseDouble(tb.getGroup(1));
            return tb.newToken(DoubleToken.FACTORY, "D", d);
        }
    }
}
