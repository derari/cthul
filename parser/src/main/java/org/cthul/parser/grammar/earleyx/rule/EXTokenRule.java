package org.cthul.parser.grammar.earleyx.rule;

import org.cthul.parser.api.Context;
import org.cthul.parser.api.Input;
import org.cthul.parser.api.Match;
import org.cthul.parser.lexer.api.TokenMatcher;

public class EXTokenRule<I extends Input<?>> extends EXRule<I> {
    
    private final TokenMatcher<? super I> tokenMatcher;

    public EXTokenRule(TokenMatcher<? super I> tokenMatcher) {
        super(tokenMatcher.getKey());
        this.tokenMatcher = tokenMatcher;
    }

    @Override
    public Match match(Context<? extends I> context, int start, int end, Match[] right) {
        assert right == null || right.length == 0 : "Right should be empty";
        return tokenMatcher.scan(context, start, end);
    }
    
}
