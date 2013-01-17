package org.cthul.parser.lexer.lazy;

import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import org.cthul.parser.api.RuleKey;
import org.cthul.parser.api.StringInput;
import org.cthul.parser.lexer.LexerBuilderBase;
import org.cthul.parser.lexer.api.MatchEval;
import org.cthul.parser.lexer.api.PlainStringInputEval;

public class LazyLexerBuilder<Token> extends LexerBuilderBase<Token, MatchResult, StringInput> {

    public LazyLexerBuilder() {
    }

    public LazyLexerBuilder(LazyLexerBuilder<Token> source) {
        super(source);
    }

    @Override
    public LazyLexer createLexer() {
        return new LazyLexer();
    }

    @Override
    public void addStringToken(RuleKey key, MatchEval<? extends Token, ? super MatchResult> eval, String string) {
        addMatcher(new LazyStringTokenMatcher(key, eval, string));
    }

    @Override
    public void addStringToken(RuleKey key, MatchEval<? extends Token, ? super MatchResult> eval, String... strings) {
        addMatcher(new LazyStringTokenMatcher(key, eval, strings));
    }

    @Override
    public void addRegexToken(RuleKey key, MatchEval<? extends Token, ? super MatchResult> eval, Pattern pattern) {
        addMatcher(new LazyRegexTokenMatcher(key, eval, pattern));
    }

    @Override
    public LazyLexerBuilder<Token> copy() {
        return new LazyLexerBuilder<>(this);
    }
    
}
