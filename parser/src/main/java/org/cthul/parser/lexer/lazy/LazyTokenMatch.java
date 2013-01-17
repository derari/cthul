package org.cthul.parser.lexer.lazy;

import org.cthul.parser.api.*;
import org.cthul.parser.lexer.api.MatchEval;
import org.cthul.parser.grammar.api.InputMatch;
import org.cthul.parser.util.Format;

public class LazyTokenMatch<V, Match> extends MatchBase<V> implements InputMatch<V> {
    
    private final RuleKey key;
    private final int start;
    private final int end;
    private final Context<? extends StringInput> context;
    private final Match match;
    private final MatchEval<? extends V, ? super Match> eval;

    public LazyTokenMatch(RuleKey key, int start, int end, 
                        Context<? extends StringInput> context, Match match, 
                        MatchEval<? extends V, ? super Match> eval) {
        this.key = key;
        this.start = start;
        this.end = end;
        this.context = context;
        this.match = match;
        this.eval = eval;
    }

    @Override
    public String getSymbol() {
        return key.getSymbol();
    }

    @Override
    public int getPriority() {
        return key.getPriority();
    }

    @Override
    public int getInputStart() {
        return start;
    }

    @Override
    public int getInputEnd() {
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
    public V eval() {
        return eval.eval(context, match, start, end);
    }

    @Override
    public String toString() {
        return super.toString() + " " + 
               Format.productionKey(getSymbol(), getPriority()) + ": " + match;
    }
    
}