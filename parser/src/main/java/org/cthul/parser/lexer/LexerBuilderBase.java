package org.cthul.parser.lexer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.cthul.parser.api.Input;
import org.cthul.parser.lexer.api.InputMatcher;

public abstract class LexerBuilderBase<Token, Match, I extends Input<?>> implements LexerBuilder<Token, Match, I> {
    
    private final List<InputMatcher<? super I>> matchers = new ArrayList<>();
    private final List<InputMatcher<? super I>> publicMatchers = Collections.unmodifiableList(matchers);

    public LexerBuilderBase() {
    }
    
    protected LexerBuilderBase(LexerBuilderBase<Token, Match, I> source) {
        this.matchers.addAll(source.matchers);
    }
    
    protected void addMatcher(InputMatcher<? super I> m) {
        matchers.add(m);
    }

    @Override
    public List<InputMatcher<? super I>> getTokenMatchers() {
        return publicMatchers;
    }
    
}
