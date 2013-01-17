package org.cthul.parser.token.lexer;

import java.util.regex.MatchResult;
import org.cthul.parser.api.Context;
import org.cthul.parser.api.StringInput;
import org.cthul.parser.lexer.api.MatchEval;
import org.cthul.parser.token.*;

/**
 * Provides a {@link TokenBuilder} to a {@link MatchEval} that creates
 * {@link Token}s, and ensures they are stored in a {@link TokenStream}.
 * @param <T> tokens created by token builder, and stored in token stream
 */
public class TokenStreamInputEval<V, T extends Token<?>> implements MatchEval<T, Object> {
    
    public static <V, T extends Token<?>> TokenStreamInputEval<V, T> wrapValue(
                String key, int channel, TokenFactory<?, ? extends T> factory, 
                MatchEval<?, ? super TokenBuilder<V, ? super T>> eval) {
        return new TokenStreamInputEval<>(
                new TokenInputEval<T, TokenBuilder<V, ? super T>>(
                key, channel, null, null));
    }

    protected final MatchEval<T, ? super TokenBuilder<V, ? super T>> eval;

    public TokenStreamInputEval(MatchEval<T, ? super TokenBuilder<V, ? super T>> eval) {
        this.eval = eval;
    }
    
    @Override
    public T eval(Context<? extends StringInput> context, Object match, int start, int end) {
        final TokenStreamBuilder<V, T> ts = getTokenStream(match, context, start, end);
        TokenBuilder<V, T> tb = ts.getTokenBuilder();
        T t = eval.eval(context, tb, start, end);
        ts.push(t);
        return t;
    }
    
    @SuppressWarnings("unchecked")
    protected TokenStreamBuilder<V, T> getTokenStream(Object match, Context<? extends StringInput> context, int start, int end) {
        final TokenStreamBuilder<V, T> ts;
        if (match instanceof TokenStreamBuilder) {
            ts = (TokenStreamBuilder) match;
        } else {
            ts = (TokenStreamBuilder) context.getOrCreate(TokenStreamBuilder.class);
            if (match instanceof String) {
                ts.setNextMatch((String) match, start);
            } else if (match instanceof MatchResult) {
                ts.setNextMatch((MatchResult) match);
            } else {
                ts.setNextMatch(start, end);
            }
        }
        return ts;
    }
}
