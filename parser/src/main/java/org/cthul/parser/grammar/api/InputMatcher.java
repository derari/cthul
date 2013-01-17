package org.cthul.parser.grammar.api;

import org.cthul.parser.api.Context;
import org.cthul.parser.api.Input;
import org.cthul.parser.api.RuleKey;

/**
 * Consumes tokens from the input to create a match for the parser.
 * @param <I> input that is consumed
 * @param <V> value that can be obtained from the match
 */
public interface InputMatcher<I extends Input<?>> {
    // <I extends Input<?>>
    // InputMatcher<? super I> tm;
    // tm.scan((Context<I>) c, _, _);
    
    RuleKey getKey();

    /**
     * Tries to match a part of the input.
     * In terms of parsing, this can be considered an atomic operation.
     * 
     * @param context
     * @param start start index, where tokens are consumed
     * @param end (optional hint)
     * @return match, if successfull
     */
    InputMatch<?> scan(Context<? extends I> context, int start, int end);
    
}
