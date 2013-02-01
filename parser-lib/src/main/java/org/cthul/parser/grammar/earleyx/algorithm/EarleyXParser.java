package org.cthul.parser.grammar.earleyx.algorithm;

import java.util.Arrays;
import org.cthul.parser.api.Context;
import org.cthul.parser.api.Input;
import org.cthul.parser.api.Match;
import org.cthul.parser.grammar.earleyx.EarleyXGrammar;
import org.cthul.parser.grammar.earleyx.rule.EXNoResult;
import org.cthul.parser.grammar.earleyx.rule.EXRule;
import org.cthul.parser.util.Format;


public abstract class EarleyXParser<I extends Input<?>> {

    protected final Context<? extends I> context;
    protected final EarleyXGrammar<I> grammar;
    
    private Match<?>[] tmp = new Match<?>[16];

    public EarleyXParser(Context<? extends I> input, EarleyXGrammar<I> grammar) {
        this.context = input;
        this.grammar = grammar;
    }
    
    public Iterable<Match<?>> parse(String startSymbol, int priority) {
        return parse(0, context.getInput().getLength(), startSymbol, priority);
    }
    
    public abstract Iterable<Match<?>> parse(int start, int end, String rule, int priority);
    
    private Match<?>[] tmpMatches(int len) {
        if (tmp.length <= len) return tmp;
        int newLen = tmp.length;
        while (newLen < len) newLen *= 2;
        return tmp = new Match<?>[newLen];
    }
    
    protected class RuleState {
        
        protected final EXRule<? super I> rule;
        protected final int ruleIndex;
        
        protected final RuleState lastState;
        protected final Match lastMatch;

        public RuleState(EXRule<? super I> rule) {
            this(rule, 0, null, null);
        }

        public RuleState(EXRule<? super I> rule, int ruleIndex, RuleState lastState, Match lastMatch) {
            this.rule = rule;
            this.ruleIndex = ruleIndex;
            this.lastState = lastState;
            this.lastMatch = lastMatch;
        }
        
        protected boolean completed() {
            return ruleIndex == rule.getLength();
        }
        
        protected String nextSymbol() {
            return rule.getSymbol(ruleIndex);
        }
        
        protected int nextPriority() {
            return rule.getPriority(ruleIndex);
        }
        
        protected Match<?> createMatch(int index) {
            assert completed();
            Match<?>[] args = collectArguments();
            int start = origin(args, index);
            return rule.match(context, start, index, args);
        }
        
        private Match<?>[] collectArguments() {
            int len = ruleIndex;
            assert len == rule.getLength() : "Should match entire rule";
            if (len == 0) return null;
            Match<?>[] tmpArgs = tmpMatches(len);
            len = fillArgs(tmpArgs);
            if (len == 0) return null;
            return Arrays.copyOf(tmpArgs, len);
        }
        
        private int fillArgs(Match<?>[] args) {
            if (ruleIndex == 0) return 0;
            int i = lastState.fillArgs(args);
            if (lastMatch instanceof EXNoResult.SkipSubtree) return i;
            args[i] = lastMatch;
            return i+1;
        }

        private int origin(Match<?>[] args, int index) {
            if (args == null || args.length == 0) return index;
            return args[0].getStartIndex();
        }
        
        protected boolean acceptNext(Match<?> match) {
            return nextPriority() <= match.getPriority() &&
                    nextSymbol().equals(match.getSymbol());
        }

        protected RuleState next(Match<?> match) {
            return new RuleState(rule, ruleIndex+1, this, match);
        }

        @Override
        public String toString() {
            return Format.productionState(
                    rule.getSymbol(), rule.getPriority(), 
                    rule.getSymbols(), rule.getPriorities(), 
                    ruleIndex);
        }

    }
    
}
