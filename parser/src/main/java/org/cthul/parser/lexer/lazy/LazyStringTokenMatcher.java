package org.cthul.parser.lexer.lazy;

import org.cthul.parser.api.Context;
import org.cthul.parser.api.RuleKey;
import org.cthul.parser.api.StringInput;
import org.cthul.parser.lexer.api.InputEval;
import org.cthul.parser.lexer.api.TokenMatch;
import org.cthul.parser.lexer.api.TokenMatcherBase;

public class LazyStringTokenMatcher extends TokenMatcherBase<StringInput> {
    
    private final InputEval<?, ? super String> eval;
    private final String[] strings;

    public LazyStringTokenMatcher(RuleKey key, InputEval<?, ? super String> eval, String... strings) {
        super(key);
        this.eval = eval;
        this.strings = strings;
    }

    @Override
    public TokenMatch scan(Context<? extends StringInput> context, int start, int end) {
        String input = context.getInput().getString();
        for (String s: strings) {
            if (input.startsWith(s, start)) {
                return new LazyTokenMatch<>(key, start, start+s.length(), context, s, eval);
            }
        }
        return null;
    }
    
}
