package org.cthul.parser.lexer;

import org.cthul.parser.TokenChannel;
import org.cthul.parser.Context;
import org.cthul.parser.NoMatchException;
import org.cthul.parser.Token;
import org.cthul.parser.TokenBuilder;
import org.cthul.parser.TokenFactory;

/**
 *
 * @author Arian Treffer
 */
public abstract class AbstractMatch implements Match {
    
    protected final String pattern;

    public AbstractMatch(String pattern) {
        this.pattern = pattern;
    }
    
    @Override
    public String getPattern() {
        return pattern;
    }

    protected String defaultKey() {
        return null;
    }

    protected int defaultChannel() {
        return TokenChannel.Undefined;
    }
    
    protected TokenFactory<?, ?> defaultFactory() {
        return null;
    }
    
    @Override
    public Token<?> invoke(TokenBuilder tokenBuilder, Context context) throws NoMatchException {
        tokenBuilder.setKey(defaultKey());
        tokenBuilder.setChannel(defaultChannel());
        final Object value = eval(tokenBuilder, context);
        return createToken(value, tokenBuilder);
    }
    
    @SuppressWarnings("unchecked")
    protected Token<?> createToken(Object value, TokenBuilder tokenBuilder) throws NoMatchException {
        boolean valueProcessed = false;
        if (value == null) {
            return tokenBuilder.getCurrentToken();
        }
        if (value.equals(Boolean.FALSE)) {
            return null;
        }
        if (value.equals(Boolean.TRUE)) {
            Token<Object> token = tokenBuilder.getCurrentToken();
            if (token != null) return token;
            valueProcessed = true;
        }
//        if (value instanceof String) {
//            tokenBuilder.setKey((String) value);
//            valueProcessed = true;
//        }
//        if (value instanceof Integer) {
//            tokenBuilder.setChannel((Integer) value);
//            valueProcessed = true;
//        }
        TokenFactory<Object, Token<Object>> f;
        if (value instanceof TokenFactory) {
            f = (TokenFactory<Object, Token<Object>>) value;
            valueProcessed = true;
        } else {
            f = (TokenFactory<Object, Token<Object>>) defaultFactory();
        }
        if (! valueProcessed) {
            throw new IllegalArgumentException(String.format(
                    "Can not create Token, unexpected value '%s'", value));
        }
        if (f != null) {
            return tokenBuilder.newToken(f);
        } else {
            return tokenBuilder.newToken();
        }
    }
    
    /**
     * The result of this function will be used to create the result of 
     * {@link Match#invoke(org.cthul.parser.TokenBuilder, org.cthul.parser.Context) invoke}.
     * If necessary, {@code tokenBuilder} will be used to create the token.
     * <ol>
     * <li>An instance of {@link Token}: this will be the result.</li>
     * <li>{@code true}: the currently configured token.</li>
     * <li>An instance of {@link String}: The value will be used as key.</li>
     * <li>An instance of {@link Integer}: The value will be used as channel.</li>
     * <li>{@code null}: the current token of {@code tokenBuilder}.</li>
     * <li>{@code false}: no token will be returned.</li>
     * </ol>
     * 
     * @param tokenBuilder
     * @param context
     * @return
     * @throws NoMatchException 
     */
    protected abstract Object eval(TokenBuilder tokenBuilder, Context context) throws NoMatchException;
    
}
