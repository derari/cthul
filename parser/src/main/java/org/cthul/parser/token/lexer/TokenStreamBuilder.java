package org.cthul.parser.token.lexer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.MatchResult;
import org.cthul.parser.api.Context;
import org.cthul.parser.api.StringInput;
import org.cthul.parser.token.*;
import org.cthul.parser.util.Initialize;
import org.cthul.parser.util.MutableMatchResult;

public class TokenStreamBuilder<V, T extends Token<?>> extends TokenStream<T> {

    private final Class<T> tokenType;
    private final Class<?> tValueType;
    private final Class<V> paramType;
    private final TokenFactory<V, ? extends T> defaultFactory;
    
    protected final List<T> main = new ArrayList<>();
    protected final MutableMatchResult mmr = new MutableMatchResult();
    protected TokenBuilderImpl<V, T> tb = null;

    private T lastPushed = null;
    
    @Initialize
    @SuppressWarnings("unchecked")
    public void init(Context<StringInput> context) {
        String input = context.getInput().getString();
        
        TokenFactory<V, ? extends T> factory = defaultFactory;
        Class<T> tType = tokenType;
        Class<?> vType = tValueType;
        Class<V> pType = paramType;
        if (factory == null) factory = context.get(TokenFactory.class);
        if (factory == null) factory = (TokenFactory) ObjectToken.defaultFactory();
        if (tType == null) tType = (Class) factory.getTokenType();
        if (vType == null) vType = factory.getTokenValueType();
        if (pType == null) pType = factory.getParameterType();
        
        tb = new TokenBuilderImpl<>(tType, vType, pType, input, mmr, factory);
        context.put(TokenBuilder.class, tb);
    }

    public TokenStreamBuilder(Class<T> tokenType, Class<?> tValueType, Class<V> paramType, TokenFactory<V, ? extends T> defaultFactory) {
        super(Collections.<T>emptyList());
        this.paramType = paramType;
        this.tValueType = tValueType;
        this.tokenType = tokenType;
        this.defaultFactory = defaultFactory;
    }

    public TokenStreamBuilder() {
        this(null, null, null, null);
    }

    @Override
    public T get(int i) {
        return main.get(i);
    }

    @Override
    public int getLength() {
        return main.size();
    }
    
    public void setNextMatch(String s, int start) {
        int end = start+s.length();
        tb.prepareNextToken(start, end);
        mmr.setString(s, start, end);
    }
    
    public void setNextMatch(MatchResult match) {
        tb.prepareNextToken(match.start(), match.end());
        mmr.setMatch(match);
    }
    
    public void setNextMatch(int start, int end) {
        tb.prepareNextToken(start, end);
        mmr.setString(tb.getString().substring(start, end), start, end);
    }
    
    // make Liskov spin
    public void push(T token) {
        if (token == lastPushed) return;
        lastPushed = token;
        if (token.getChannel() == TokenChannel.Default) {
            main.add(token);
        }
        all.add(token);
    }
    
    public TokenBuilder<V, T> getTokenBuilder() {
        return tb;
    }
    
    protected class TokenBuilderImpl<V, T extends Token<?>> extends AbstractTokenBuilder<V, T> {

        public TokenBuilderImpl(String input, MatchResult groupProvider, TokenFactory<V, T> defaultFactory) {
            super(input, groupProvider, defaultFactory);
        }

        public TokenBuilderImpl(Class<T> tokenType, Class<?> tValueType, Class<V> paramType, String input, MatchResult groupProvider, TokenFactory<? super V, ? extends T> defaultFactory) {
            super(tokenType, tValueType, paramType, input, groupProvider, defaultFactory);
        }

        @Override
        protected void prepareNextToken(int start, int end) {
            super.prepareNextToken(start, end);
        }
        
        @Override
        public int getDefaultIndex() {
            return main.size();
        }

        @Override
        public int getInputIndex() {
            return all.size();
        }
    }
}
