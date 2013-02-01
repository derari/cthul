package org.cthul.parser.token.lexer;

import org.cthul.parser.api.Context;
import org.cthul.parser.api.StringInput;
import org.cthul.parser.lexer.api.InputEval;
import org.cthul.parser.token.*;

/**
 * Wraps a {@link InputEval} that expects a token builder.
 * Pre-configures the token builder with default values, and ensures
 * that the result is convert into a {@link Token}.
 */
public class TokenInputEval<T extends Token<?>, TB extends TokenBuilder<?, ? super T>> implements InputEval<T, TB> {
    
    protected final InputEval<?, ? super TB> eval;
    protected final String key;
    protected final int channel;
    protected final TokenFactory<?, ? extends T> factory;

    public TokenInputEval(String key, int channel, TokenFactory<?, ? extends T> factory, InputEval<?, ? super TB> eval) {
        this.eval = eval;
        this.key = key;
        this.channel = channel;
        this.factory = factory;
    }
    
    @Override
    public T eval(Context<? extends StringInput> context, TB tb, int start, int end) {
        if (key != null && !key.isEmpty()) tb.setKey(key);
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
