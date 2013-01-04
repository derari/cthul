package org.cthul.parser.token;

import org.cthul.parser.api.Context;
import org.cthul.parser.api.RuleKey;
import org.cthul.parser.api.TokenInput;
import org.cthul.parser.lexer.api.TokenMatch;
import org.cthul.parser.lexer.api.TokenMatcherBase;

public class SingleTokenMatcher extends TokenMatcherBase<TokenInput<? extends Token>> {

    public SingleTokenMatcher(RuleKey key) {
        super(key);
    }

    @Override
    public TokenMatch scan(Context<? extends TokenInput<? extends Token>> context, int start, int end) {
        TokenInput<? extends Token> input = context.getInput();
        Token token = input.get(start);
        if (key.equals(token.getKey())) {
            return new SingleTokenMatch(start, token);
        }
        return null;
    }
    
}
