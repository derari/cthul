package org.cthul.parser.lexer.simple;

import org.cthul.parser.api.RuleKey;
import org.cthul.parser.api.TokenInput;
import org.cthul.parser.lexer.api.TokenMatcher;
import org.cthul.parser.token.SingleTokenMatcher;
import org.cthul.parser.token.Token;

public class SimpleTokenBuilderFactory extends SimpleLexerBuilderBase<Token> {

    public SimpleTokenBuilderFactory() {
    }

    public SimpleTokenBuilderFactory(SimpleLexerBuilderBase source) {
        super(source);
    }

    @Override
    protected TokenMatcher<? super TokenInput<Token>> createMatcher(RuleKey key) {
        return new SingleTokenMatcher(key);
    }

    @Override
    public SimpleTokenBuilderFactory copy() {
        return new SimpleTokenBuilderFactory(this);
    }
    
}
