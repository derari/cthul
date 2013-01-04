package org.cthul.parser.lexer.api;

import org.cthul.parser.api.Context;
import org.cthul.parser.api.Input;
import org.cthul.parser.api.RuleKey;

/**
 * Consumes tokens from the token stream to create a match for the parser.
 * @param <I> input that is consumed
 */
public interface TokenMatcher<I extends Input<?>> {
    // <I extends Input<?>>
    // TokenMatcher<? super I> tm;
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
    TokenMatch scan(Context<? extends I> context, int start, int end);
    
}
