package org.cthul.parser.lexer;

import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import org.cthul.parser.api.Input;
import org.cthul.parser.api.RuleKey;
import org.cthul.parser.lexer.api.InputEval;
import org.cthul.parser.lexer.api.TokenMatcher;

/**
 * @param <Token> tokens produced by {@link InputEval}s
 * @param <I> input produced by lexer
 */
public interface LexerBuilder<Token, I extends Input<?>> {

    void addRegexToken(RuleKey key, InputEval<? extends Token, ? super MatchResult> eval, Pattern pattern);

    void addStringToken(RuleKey key, InputEval<? extends Token, ? super String> eval, String string);

    void addStringToken(RuleKey key, InputEval<? extends Token, ? super String> eval, String... strings);
    
    List<TokenMatcher<? super I>> getTokenMatchers();

    Lexer<? extends I> createLexer();
    
    LexerBuilder<Token, I> copy();
    
}
