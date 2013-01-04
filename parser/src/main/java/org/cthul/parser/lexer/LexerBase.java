package org.cthul.parser.lexer;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.cthul.parser.api.Input;
import org.cthul.parser.lexer.api.TokenMatcher;

public abstract class LexerBase<I extends Input<?>> implements Lexer<I> {
    
    private List<TokenMatcher<? super I>> matchers;

    public LexerBase(Collection<? extends TokenMatcher<? super I>> matchers) {
        this.matchers = (List) Arrays.asList(matchers.toArray());
    }

    @Override
    public List<TokenMatcher<? super I>> getTokenMatchers() {
        return matchers;
    }
    
}
