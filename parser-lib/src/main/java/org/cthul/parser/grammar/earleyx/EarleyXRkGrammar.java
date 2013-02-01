package org.cthul.parser.grammar.earleyx;

import org.cthul.parser.api.Context;
import org.cthul.parser.api.Input;
import org.cthul.parser.grammar.earleyx.algorithm.EarleyXRkParser;


public class EarleyXRkGrammar<I extends Input<?>> extends EarleyXGrammar<I> {

    public EarleyXRkGrammar() {
    }

    @Override
    public EarleyXRkParser<I> parser(Context<? extends I> context) {
        return new EarleyXRkParser<>(context, this);
    }
    
}
