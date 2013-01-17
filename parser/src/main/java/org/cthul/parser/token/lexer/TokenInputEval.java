package org.cthul.parser.token.lexer;

import org.cthul.parser.api.Context;
import org.cthul.parser.api.StringInput;
import org.cthul.parser.lexer.api.MatchEval;
import org.cthul.parser.token.*;

/**
 * Wraps a {@link MatchEval} that expects a token builder.
 * Pre-configures the token builder with default values, and ensures
 * that the result is convert into a {@link Token}.
 */
public class TokenInputEval<T extends Token<?>, TB extends TokenBuilder<?, ? super T>> implements MatchEval<T, TB> {
    
//    {
//        MatchEval<?, TokenBuilder<Object, Token<? extends Object>>> m = null;
//        TokenFactory<Double, Token<Double>> tf = null;
//        TokenInputEval<Token<? extends Object>, TokenBuilder<Object, Token<? extends Object>>> te = 
//                new TokenInputEval<Token<? extends Object>, TokenBuilder<Object, Token<? extends Object>>>(
//                null, -1, tf, m);
//        
//        MatchEval<?, TokenBuilder<?, Token<? super Double>>> m2 = null;
//        TokenFactory<Double, Token<Double>> tf2 = null;
//        TokenInputEval<Token<? extends Object>, TokenBuilder<Object, Token<? extends Object>>> te2 = 
//                new TokenInputEval<Token<? extends Object>, TokenBuilder<Object, Token<? extends Object>>>(
//                null, -1, tf, m);
//    }

    protected final MatchEval<?, ? super TB> eval;
    protected final String key;
    protected final int channel;
    protected final TokenFactory<?, ? extends T> factory;

    public TokenInputEval(String key, int channel, TokenFactory<?, ? extends T> factory, MatchEval<?, ? super TB> eval) {
        this.eval = eval;
        this.key = key;
        this.channel = channel;
        this.factory = factory;
    }
    
    @Override
    public T eval(Context<? extends StringInput> context, TB tb, int start, int end) {
        if (key != null) tb.setKey(key);
        if (channel != TokenChannel.Undefined) tb.setChannel(channel);
        if (factory != null) tb.setFactory(factory);
        
        Object result = eval.eval(context, tb, start, end);
        return convert(context, tb, result);
    }
    
    @SuppressWarnings("unchecked")
    protected T convert(Context<? extends StringInput> context, TokenBuilder<?, ? super T> tb, Object result) {
        if (result == null) {
            return (T) tb.getCurrentToken();
        }
        if (result instanceof Token) {
            T token = (T) result;
            tb.setCurrentToken(token);
            return token;
        }
        if (result instanceof TokenFactory) {
            return tb.newToken((TokenFactory<Object, ? extends T>) result);
        }
        if (result instanceof String) {
            tb.setMatch((String) result);
            return (T) tb.newToken();
        }
        ((TokenBuilder<Object, ?>) tb).setValue(result);
        return (T) tb.newToken();
    }
    
}
