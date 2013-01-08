package org.cthul.parser.token;

import org.cthul.parser.api.Context;
import org.cthul.parser.api.RuleKey;
import org.cthul.parser.api.TokenInput;
import org.cthul.parser.lexer.api.TokenMatcherBase;

public class TokenMatcher extends TokenMatcherBase<TokenInput<? extends Token>> {

    public TokenMatcher(RuleKey key) {
        super(key);
    }
    
    @Override
    public Token scan(Context<? extends TokenInput<? extends Token>> context, int start, int end) {
        TokenInput<? extends Token> input = context.getInput();
        Token t = input.get(start);
        if (t.match(key)) return t;
        return null;
    }

}
