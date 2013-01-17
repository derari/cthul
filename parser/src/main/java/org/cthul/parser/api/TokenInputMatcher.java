package org.cthul.parser.api;

import org.cthul.parser.grammar.api.InputMatch;
import org.cthul.parser.grammar.api.InputMatcherBase;

public class TokenInputMatcher extends InputMatcherBase<TokenInput<? extends InputMatch<?>>> {

    public TokenInputMatcher(RuleKey key) {
        super(key);
    }

    @Override
    public InputMatch<?> scan(Context<? extends TokenInput<? extends InputMatch<?>>> context, int start, int end) {
        TokenInput<? extends InputMatch<?>> input = context.getInput();
        RuleKey key = getKey();
        InputMatch<?> m = input.get(start);
        if (m.getPriority() >= key.getPriority() && 
                m.getSymbol().equals(key.getSymbol())) {
            return m;
        }
        return null;
    }

}
