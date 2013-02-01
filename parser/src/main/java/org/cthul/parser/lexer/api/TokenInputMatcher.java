package org.cthul.parser.lexer.api;

import org.cthul.parser.api.Context;
import org.cthul.parser.api.RuleKey;
import org.cthul.parser.api.TokenInput;

public class TokenInputMatcher extends InputMatcherBase<TokenInput<? extends InputMatch<?>>> {

    public static final TokenInputMatcher MATCH_ANY = new TokenInputMatcher();
    
    private boolean anyKey;

    protected TokenInputMatcher() {
        super(new RuleKey("", 0));
        this.anyKey = true;
    }
    
    public TokenInputMatcher(RuleKey key) {
        super(key);
        anyKey = false;
    }

    @Override
    public InputMatch<?> scan(Context<? extends TokenInput<? extends InputMatch<?>>> context, int start, int end) {
        TokenInput<? extends InputMatch<?>> input = context.getInput();
        if (start >= input.getLength()) return null;
        InputMatch<?> m = input.get(start);
        if (anyKey) return m;
        RuleKey key = getKey();
        if (// m.getPriority() >= key.getPriority() && 
            // token only match by key
                m.getSymbol().equals(key.getSymbol())) {
            return m;
        }
        return null;
    }

}
