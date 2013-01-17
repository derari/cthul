package org.cthul.parser.lexer.api;

import org.cthul.parser.api.Context;
import org.cthul.parser.api.StringInput;

/**
 * Converts a lexer match into a token.
 *
 * @param <Token> tokens that are produced
 * @param <Match> matches that are consumed
 */
public interface MatchEval<Token, Match> {
    // MatchEval<? extends T, ? super M> eval;
    // T t = eval.eval(_, (M) m, _, _);
    
    Token eval(Context<? extends StringInput> context, Match match, int start, int end);
    
}
