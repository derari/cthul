package org.cthul.parser.lexer.lazy;

import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.cthul.parser.api.Context;
import org.cthul.parser.api.RuleKey;
import org.cthul.parser.api.StringInput;
import org.cthul.parser.lexer.api.InputEval;
import org.cthul.parser.lexer.api.TokenMatch;
import org.cthul.parser.lexer.api.TokenMatcherBase;

public class LazyRegexTokenMatcher extends TokenMatcherBase<StringInput> {
    
    private final InputEval<?, ? super MatchResult> eval;
    private final Pattern pattern;

    public LazyRegexTokenMatcher(RuleKey key, InputEval<?, ? super MatchResult> eval, Pattern pattern) {
        super(key);
        this.eval = eval;
        this.pattern = pattern;
    }

    @Override
    public TokenMatch scan(Context<? extends StringInput> context, int start, int end) {
        String input = context.getInput().getString();
        Matcher m = pattern.matcher(input);
        if (!m.find(start) || m.start() != start) return null;
        return new LazyTokenMatch<>(key, start, m.end(), context, m, eval);
    }
    
}
