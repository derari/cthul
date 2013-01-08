package org.cthul.parser.lexer.api;

import java.util.regex.MatchResult;
import org.cthul.parser.api.Context;
import org.cthul.parser.api.StringInput;
import org.cthul.parser.lexer.api.InputEval;

public class PlainStringInputEval implements InputEval<String, Object> {
    
    private static final PlainStringInputEval INSTANCE = new PlainStringInputEval();
    
    public static PlainStringInputEval instance() {
        return INSTANCE;
    }

    @Override
    public String eval(Context<? extends StringInput> context, Object match, int start, int end) {
        if (match instanceof MatchResult) {
            return ((MatchResult) match).group();
        } else {
            return match.toString();
        }
    }
    
}
