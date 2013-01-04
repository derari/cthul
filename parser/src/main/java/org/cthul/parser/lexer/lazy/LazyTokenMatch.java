package org.cthul.parser.lexer.lazy;

import org.cthul.parser.api.Context;
import org.cthul.parser.api.StringInput;
import org.cthul.parser.lexer.api.InputEval;
import org.cthul.parser.lexer.api.TokenMatch;

public class LazyTokenMatch<Match> implements TokenMatch {
    
    private final int start;
    private final int end;
    private final Context<? extends StringInput> context;
    private final Match match;
    private final InputEval<?, Match> eval;

    public LazyTokenMatch(int start, int end, Context<? extends StringInput> context, Match match, InputEval<?, Match> eval) {
        this.start = start;
        this.end = end;
        this.context = context;
        this.match = match;
        this.eval = eval;
    }

    @Override
    public int getStart() {
        return start;
    }

    @Override
    public int getEnd() {
        return end;
    }

    @Override
    public int getStartIndex() {
        return start;
    }

    @Override
    public int getEndIndex() {
        return end;
    }

    @Override
    public Object eval() {
        return eval.eval(context, match, start, end);
    }
    
}
