package org.cthul.parser.grammar.earleyx;

import org.cthul.parser.api.Input;


public class EarleyXRkGrammarBuilder<I extends Input<?>> extends EarleyXGrammarBuilder<I> {

    public EarleyXRkGrammarBuilder() {
    }

    public EarleyXRkGrammarBuilder(EarleyXGrammarBuilder<I> source) {
        super(source);
    }

    @Override
    protected EarleyXRkGrammar<? super I> newGrammar() {
        return new EarleyXRkGrammar<>();
    }

    @Override
    public EarleyXRkGrammarBuilder<I> copy() {
        return new EarleyXRkGrammarBuilder<>(this);
    }
    
}
