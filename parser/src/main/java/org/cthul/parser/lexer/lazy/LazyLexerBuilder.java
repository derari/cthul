package org.cthul.parser.lexer.lazy;

import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import org.cthul.parser.api.RuleKey;
import org.cthul.parser.api.StringInput;
import org.cthul.parser.lexer.Lexer;
import org.cthul.parser.lexer.LexerBuilder;
import org.cthul.parser.lexer.LexerBuilderBase;
import org.cthul.parser.lexer.api.InputEval;

public class LazyLexerBuilder extends LexerBuilderBase<Object, StringInput> {

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
    public void addStringToken(RuleKey key, InputEval<?, String> eval, String string) {
        addMatcher(new LazyStringTokenMatcher(key, eval, string));
    }

    @Override
    public void addStringToken(RuleKey key, InputEval<?, String> eval, String... strings) {
        addMatcher(new LazyStringTokenMatcher(key, eval, strings));
    }

    @Override
    public void addRegexToken(RuleKey key, InputEval<?, MatchResult> eval, Pattern pattern) {
        addMatcher(new LazyRegexTokenMatcher(key, eval, pattern));
    }

    @Override
    public LexerBuilder<Object, StringInput> copy() {
        return new LazyLexerBuilder(this);
    }
    
}
