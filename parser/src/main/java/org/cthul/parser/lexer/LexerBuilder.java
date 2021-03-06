package org.cthul.parser.lexer;

import java.util.List;
import java.util.regex.Pattern;
import org.cthul.parser.api.Input;
import org.cthul.parser.api.RuleKey;
import org.cthul.parser.lexer.api.InputMatcher;
import org.cthul.parser.lexer.api.InputEval;

/**
 * @param <Token> tokens produced by {@link InputEval}s
 * @param <Match> matching information provided by lexer
 * @param <I> input produced by lexer
 */
public interface LexerBuilder<Token, Match, I extends Input<?>> {

    void addRegexToken(RuleKey key, InputEval<? extends Token, ? super Match> eval, Pattern pattern);

    void addStringToken(RuleKey key, InputEval<? extends Token, ? super Match> eval, String string);

    void addStringToken(RuleKey key, InputEval<? extends Token, ? super Match> eval, String... strings);
    
    List<InputMatcher<? super I>> getTokenMatchers();

    Lexer<? extends I> createLexer();
    
    LexerBuilder<Token, Match, I> copy();
    
}
