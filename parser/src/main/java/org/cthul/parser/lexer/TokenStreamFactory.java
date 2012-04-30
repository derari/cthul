package org.cthul.parser.lexer;

import org.cthul.parser.TokenChannel;
import org.cthul.parser.Context;
import org.cthul.parser.Token;
import org.cthul.parser.TokenBuilder;
import org.cthul.parser.TokenFactory;
import org.cthul.parser.TokenStream;

/**
 *
 * @author Arian Treffer
 */
public class TokenStreamFactory {
    
    private final TokenBuilderImpl tokenBuilder;
    private final SimpleTokenStream tokenStream;
    private final String input;
    private Token<?> lastToken = null;

    public TokenStreamFactory(String input, TokenFactory<?, ?> defaultFactory, TokenBuilder.GroupProvider groupProvider) {
        this(input, defaultFactory, null, groupProvider);
    }
    
    public <V, T extends Token<V>> TokenStreamFactory(String input, TokenFactory<V, T> defaultFactory, Context context, TokenBuilder.GroupProvider groupProvider) {
        this.input = input;
        tokenStream  = new SimpleTokenStream(input);
        tokenBuilder = new TokenBuilderImpl(input, defaultFactory, groupProvider);
        if (context != null) {
            context.put(TokenBuilder.class, tokenBuilder);
            context.put(TokenStream.class, tokenStream);
        }
    }
    
    public TokenBuilder getTokenBuilder() {
        return tokenBuilder;
    }

    public TokenStream getTokenStream() {
        return tokenStream;
    }
    
    public boolean prepareNext() {
        if (tokenBuilder.getEnd() >= input.length()) return false;
        tokenBuilder.prepareNextToken();
        return true;
    }
    
    public void setUpNext(int length) {
        tokenBuilder.setLength(length);
        tokenBuilder.setKey(null);
        tokenBuilder.setChannel(TokenChannel.Undefined);
    }
    
    public void pushToken(Token<?> token) {
        lastToken = token;
        tokenStream.add(token);
    }

    public Token<?> getLastToken() {
        return lastToken;
    }
    
    private class TokenBuilderImpl extends AbstractTokenBuilder {

        public TokenBuilderImpl(String input, TokenFactory<?, ?> defaultFactory, GroupProvider groupProvider) {
            super(input, defaultFactory, groupProvider);
        }
        
        void prepareNextToken() {
            final int end = getEnd();
            setMatch(end, end);
            setKey(null);
        }
        
        @Override
        public int getMatchIndex() {
            return tokenStream.nextFreeId();
        }

    }

}
