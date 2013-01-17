package org.cthul.parser.token;

import java.util.regex.MatchResult;
import org.cthul.parser.util.StringMatchResult;
import org.cthul.proc.Proc2;
import org.cthul.proc.Proc3;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.cthul.matchers.CthulMatchers.*;
import static org.cthul.proc.Procs.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class AbstractTokenBuilderTest {
    
    private static final String S = "";
    private static final MatchResult MR = new StringMatchResult("", 0);
    private static <V> TokenFactory<V, Token<V>> TF() {
        return ObjectToken.defaultFactory();
    }
    
    private static Proc2<TokenBuilder<?,?>, Object> call_setValue = invoke(TokenBuilder.class, "setValue").asProc2();
    private static Proc2<TokenBuilder<?,?>, Object> call_setFactory = invoke(TokenBuilder.class, "setFactory").asProc2();
    private static Proc2<TokenBuilder<?,?>, Object> call_newToken = invoke(TokenBuilder.class, "newToken", noParameters()).asProc2();
    private static Proc3<TokenBuilder<?,?>, Object, Object> call_newToken_F = invoke(TokenBuilder.class, "newToken", TokenFactory.class).asProc3();
    
    public AbstractTokenBuilderTest() {
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
    @SuppressWarnings("unchecked")
    public void test_explicit_param_type() {
        TokenBuilder tb = new TestTokenBuilder(null, null, Double.class, S, MR, TF());
        
        // should only allow double value
        assertThat(call_setValue.with(tb, 2.0), returns());
        assertThat(call_setValue.with(tb, 2), raises(ClassCastException.class, "Invalid argument"));
    }
     
    @Test
    @SuppressWarnings("unchecked")
    public void test_explicit_value_type() {
        TokenBuilder tb = new TestTokenBuilder(null, String.class, null, S, MR, TF());
        
        // Double allowed as argument, but not as token value
        assertThat(call_setValue.with(tb, 2.0), returns());
        assertThat(call_newToken.with(tb), raises(ClassCastException.class, "Invalid token value type"));
    }
     
    @Test
    @SuppressWarnings("unchecked")
    public void test_explicit_token_type() {
        TokenBuilder tb = new TestTokenBuilder(ObjectToken.class, null, null, S, MR, ObjectToken.FACTORY);
        
        assertThat(call_setFactory.with(tb, StringToken.FACTORY), returns());
        
        // using a token factory that does not produce ObjectTokens
        assertThat(call_setFactory.with(tb, TestToken.FACTORY), raises(ClassCastException.class, "Invalid token type"));
        assertThat(call_newToken_F.with(tb, TestToken.FACTORY), raises(ClassCastException.class, "Invalid token"));
    }
     
    @Test
    @SuppressWarnings("unchecked")
    public void test_factory_param_type() {
        TokenBuilder tb = new TestTokenBuilder(null, null, null, S, MR, TF());
        tb.setValue(2.0);

        // configuring factory that cannot accept Double
        assertThat(call_setFactory.with(tb, IntegerToken.FACTORY), returns());
        assertThat(call_newToken.with(tb), raises(ClassCastException.class, "Invalid argument"));
    }
    
    public static class TestTokenBuilder<V, T extends Token<? extends V>> extends AbstractTokenBuilder<V, T> {

        public TestTokenBuilder(String input, MatchResult groupProvider, TokenFactory<V, T> defaultFactory) {
            super(input, groupProvider, defaultFactory);
        }

        public TestTokenBuilder(Class<T> tokenType, Class<?> tValueType, Class<V> paramType, String input, MatchResult groupProvider, TokenFactory<? super V, ? extends T> defaultFactory) {
            super(tokenType, tValueType, paramType, input, groupProvider, defaultFactory);
        }

        @Override
        public int getDefaultIndex() {
            return 1;
        }

        @Override
        public int getInputIndex() {
            return 1;
        }
        
    }
}
