package org.cthul.parser.token;

import org.cthul.parser.lexer.api.TokenMatch;

public class SingleTokenMatch implements TokenMatch {
    
    private final int index;
    private final Token token;

    public SingleTokenMatch(int index, Token token) {
        this.index = index;
        this.token = token;
    }
    
    @Override
    public int getStart() {
        return token.getStart();
    }

    @Override
    public int getEnd() {
        return token.getEnd();
    }

    @Override
    public int getStartIndex() {
        return index;
    }

    @Override
    public int getEndIndex() {
        return index+1;
    }

    @Override
    public Object eval() {
        return token;
    }
    
}
