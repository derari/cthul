package org.cthul.parser.grammar;

import org.cthul.parser.api.Context;
import org.cthul.parser.api.Input;
import org.cthul.parser.api.RuleKey;

/**
 * 
 * @param <I> input that is consumed
 */
public interface Grammar<I extends Input<?>> {
    
    Object parse(Context<? extends I> context, RuleKey startSymbol);
    
}
