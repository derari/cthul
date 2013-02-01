package org.cthul.parser.token.lexer;

import java.util.regex.MatchResult;
import org.cthul.parser.api.Context;
import org.cthul.parser.api.StringInput;
import org.cthul.parser.lexer.api.InputEval;
import org.cthul.parser.token.*;

/**
 * Provides a {@link TokenBuilder} to a {@link InputEval} that creates
 * {@link Token}s, and ensures they are stored in a {@link TokenStream}.
 * @param <T> tokens created by token builder, and stored in token stream
 */
public class TokenStreamInputEval<T extends Token<?>, TB extends TokenBuilder<?, ? super T>> implements InputEval<T, Object> {
    
    public static <T extends Token<?>, TB extends TokenBuilder<?, ? super T>> TokenStreamInputEval<T, TB> wrapValue(
                String key, int channel, TokenFactory<?, ? extends T> factory, 
                InputEval<?, ? super TB> eval) {
        return new TokenStreamInputEval<>(
                new TokenInputEval<>(
                    key, channel, factory, eval));
    }
    
    public static <T extends Token<?>, TB extends TokenBuilder<?, ? super T>> TokenStreamInputEval<T, TB> wrapValue(
                InputEval<?, ? super TB> eval) {
        return wrapValue(null, TokenChannel.Default, null, eval);
    }
    
    protected final InputEval<T, ? super TB> eval;

    public TokenStreamInputEval(InputEval<T, ? super TB> eval) {
        this.eval = eval;
    }
    
    @Override
    public T eval(Context<? extends StringInput> context, Object match, int start, int end) {
        final TokenStreamBuilder<?, ? super T> ts = getTokenStream(match, context, start, end);
        TB tb = (TB) ts.getTokenBuilder();
        T t = eval.eval(context, tb, start, end);
        ts.push(t);
        return t;
    }
    
    @SuppressWarnings("unchecked")
    protected TokenStreamBuilder<?, ? super T> getTokenStream(Object match, Context<? extends StringInput> context, int start, int end) {
        final TokenStreamBuilder ts;
        if (match instanceof TokenStreamBuilder) {
            ts = (TokenStreamBuilder) match;
        } else {
            ts = (TokenStreamBuilder) context.getOrCreate(TokenStreamBuilder.class);
            if (match instanceof String) {
                ts.setNextMatch(null, null, (String) match, start);
            } else if (match instanceof MatchResult) {
                ts.setNextMatch(null, null, (MatchResult) match);
            } else {
                ts.setNextMatch(null, null, start, end);
            }
        }
        return ts;
    }
}
