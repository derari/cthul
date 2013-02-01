package org.cthul.parser.grammar.earleyx;

import org.cthul.parser.api.Context;
import org.cthul.parser.api.Input;
import org.cthul.parser.grammar.earleyx.algorithm.EarleyXItParser;


public class EarleyXItGrammar<I extends Input<?>> extends EarleyXGrammar<I> {

    public EarleyXItGrammar() {
    }

    @Override
    public EarleyXItParser<I> parser(Context<? extends I> context) {
        return new EarleyXItParser<>(context, this);
    }
    
}
