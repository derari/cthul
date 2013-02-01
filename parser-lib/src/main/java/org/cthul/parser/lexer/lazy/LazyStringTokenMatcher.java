package org.cthul.parser.lexer.lazy;

import java.util.regex.MatchResult;
import org.cthul.parser.api.Context;
import org.cthul.parser.api.RuleKey;
import org.cthul.parser.api.StringInput;
import org.cthul.parser.lexer.api.InputEval;
import org.cthul.parser.lexer.api.InputMatch;
import org.cthul.parser.lexer.api.InputMatcherBase;
import org.cthul.parser.util.StringMatchResult;

public class LazyStringTokenMatcher extends InputMatcherBase<StringInput> {
    
    private final InputEval<?, ? super MatchResult> eval;
    private final String[] strings;

    public LazyStringTokenMatcher(RuleKey key, InputEval<?, ? super MatchResult> eval, String... strings) {
        super(key);
        this.eval = eval;
        this.strings = strings;
    }

    @Override
    public InputMatch<?> scan(Context<? extends StringInput> context, int start, int end) {
        String input = context.getInput().getString();
        for (String s: strings) {
            if (input.startsWith(s, start)) {
                MatchResult mr = new StringMatchResult(s, start);
                return new LazyTokenMatch<>(key, start, mr.end(), context, mr, eval);
            }
        }
        return null;
    }
    
}
