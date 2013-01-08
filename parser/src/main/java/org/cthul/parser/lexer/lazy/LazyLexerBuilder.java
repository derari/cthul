package org.cthul.parser.lexer.lazy;

import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import org.cthul.parser.api.RuleKey;
import org.cthul.parser.api.StringInput;
import org.cthul.parser.lexer.Lexer;
import org.cthul.parser.lexer.LexerBuilder;
import org.cthul.parser.lexer.LexerBuilderBase;
import org.cthul.parser.lexer.api.InputEval;

public class LazyLexerBuilder<Token> extends LexerBuilderBase<Token, StringInput> {

    public LazyLexerBuilder() {
    }

    public LazyLexerBuilder(LazyLexerBuilder source) {
        super(source);
    }

    @Override
    public Lexer<StringInput> createLexer() {
        return new LazyLexer(getTokenMatchers());
    }

    @Override
    public void addStringToken(RuleKey key, InputEval<? extends Token, ? super String> eval, String string) {
        addMatcher(new LazyStringTokenMatcher(key, eval, string));
    }

    @Override
    public void addStringToken(RuleKey key, InputEval<? extends Token, ? super String> eval, String... strings) {
        addMatcher(new LazyStringTokenMatcher(key, eval, strings));
    }

    @Override
    public void addRegexToken(RuleKey key, InputEval<? extends Token, ? super MatchResult> eval, Pattern pattern) {
        addMatcher(new LazyRegexTokenMatcher(key, eval, pattern));
    }

    @Override
    public LexerBuilder<Token, StringInput> copy() {
        return new LazyLexerBuilder(this);
    }
    
}
