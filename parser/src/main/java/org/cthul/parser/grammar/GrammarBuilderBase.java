package org.cthul.parser.grammar;

import java.util.List;
import org.cthul.parser.api.Input;
import org.cthul.parser.grammar.api.InputMatcher;

public abstract class GrammarBuilderBase<I extends Input<?>> implements GrammarBuilder<I> {
    
    private boolean inputMatchersSet = false;

    public GrammarBuilderBase() {
    }
    
    public GrammarBuilderBase(GrammarBuilderBase<I> source) {
        this.inputMatchersSet = source.inputMatchersSet;
    }

    @Override
    public void setInputMatchers(List<InputMatcher<? super I>> inputMatchers) {
        if (inputMatchersSet) {
            throw new IllegalStateException("Token matchers already set.");
        }
        inputMatchersSet = true;
        generateInputRules(inputMatchers);
    }
    
    protected abstract void generateInputRules(List<InputMatcher<? super I>> tokenMatchers);
    
}
