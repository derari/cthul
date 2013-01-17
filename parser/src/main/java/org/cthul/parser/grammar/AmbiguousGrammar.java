package org.cthul.parser.grammar;

import org.cthul.parser.api.Context;
import org.cthul.parser.api.Input;
import org.cthul.parser.api.RuleKey;

public interface AmbiguousGrammar<I extends Input<?>> extends Grammar<I> {
    
    @Override
    Iterable<?> parse(Context<? extends I> context, RuleKey startSymbol, Object arg);
    
}
