package org.cthul.parser.grammar.earleyx.algorithm;

import org.cthul.parser.api.Context;
import org.cthul.parser.api.Match;
import org.cthul.parser.grammar.earleyx.EarleyXGrammar;
import org.cthul.parser.grammar.earleyx.rule.EXRule;
import org.cthul.parser.util.Format;


public abstract class EarleyXParser {

    protected final Context<?> Context;
    protected final EarleyXGrammar grammar;

    public EarleyXParser(Context<?> input, EarleyXGrammar grammar) {
        this.Context = input;
        this.grammar = grammar;
    }
    
    public Iterable<Match> parse(String startSymbol, int priority) {
        return parse(0, Context.getInput().getLength(), startSymbol, priority);
    }
    
    public abstract Iterable<Match> parse(int start, int end, String rule, int priority);
    
    protected class RuleState {
        
        protected final EXRule rule;
        protected final int ruleIndex;
        
        protected final RuleState lastState;
        protected final Match lastMatch;

        public RuleState(EXRule rule) {
            this(rule, 0, null, null);
        }

        public RuleState(EXRule rule, int ruleIndex, RuleState lastState, Match lastMatch) {
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
        
        protected Match createMatch(int index) {
            assert completed();
            Match[] args = collectArguments();
            int start = origin(args, index);
            return rule.match(Context, start, index, args);
        }
        
        private Match[] collectArguments() {
            int len = ruleIndex;
            assert len == rule.getLength() : 
                   "Should match entire rule";
            if (len == 0) return null;
            Match[] args = new Match[len];
            fillArgs(args);
            return args;
        }
        
        private void fillArgs(Match[] args) {
            if (ruleIndex == 0) return;
            lastState.fillArgs(args);
            args[ruleIndex-1] = lastMatch;
        }

        private int origin(Match[] args, int index) {
            if (args == null || args.length == 0) return index;
            return args[0].getStartIndex();
        }
        
        protected boolean acceptNext(Match match) {
            return nextPriority() <= match.getPriority() &&
                    nextSymbol().equals(match.getSymbol());
        }

        protected RuleState next(Match match) {
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
