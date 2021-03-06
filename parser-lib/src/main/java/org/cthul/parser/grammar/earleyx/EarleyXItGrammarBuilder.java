package org.cthul.parser.grammar.earleyx;

import org.cthul.parser.api.Input;


public class EarleyXItGrammarBuilder<I extends Input<?>> extends EarleyXGrammarBuilder<I> {

    public EarleyXItGrammarBuilder() {
    }

    protected EarleyXItGrammarBuilder(EarleyXGrammarBuilder<I> source) {
        super(source);
    }

    @Override
    protected EarleyXItGrammar<I> newGrammar() {
        return new EarleyXItGrammar<>();
    }

    @Override
    public EarleyXItGrammarBuilder<I> copy() {
        return new EarleyXItGrammarBuilder<>(this);
    }
    
}
