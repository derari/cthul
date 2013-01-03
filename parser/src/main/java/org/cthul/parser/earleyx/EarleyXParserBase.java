package org.cthul.parser.earleyx;

import org.cthul.parser.earleyx.api.EXRule;
import org.cthul.parser.earleyx.api.EXMatch;
import java.util.*;
import org.cthul.parser.api.Context;
import org.cthul.parser.earleyx.api.EXAntiMatchRule;
import org.cthul.parser.util.*;

public abstract class EarleyXParserBase {

    protected final Context<?> input;
    protected final EarleyXGrammar grammar;

    public EarleyXParserBase(Context<?> input, EarleyXGrammar grammar) {
        this.input = input;
        this.grammar = grammar;
    }
    
    public Iterable<EXMatch> parse(String startSymbol) {
        return parse(0, input.getInput().getLength(), startSymbol, 0);
    }
    
    public abstract Iterable<EXMatch> parse(int start, int end, String rule, int priority);
    
    protected class RuleState {
        
        protected final EXRule rule;
        protected final int ruleIndex;
        
        protected final RuleState lastState;
        protected final EXMatch lastMatch;

        public RuleState(EXRule rule) {
            this(rule, 0, null, null);
        }

        public RuleState(EXRule rule, int ruleIndex, RuleState lastState, EXMatch lastMatch) {
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
        
        protected EXMatch createMatch(int index) {
            assert completed();
            EXMatch[] args = collectArguments();
            int start = origin(args, index);
            return rule.match(input, start, index, args);
        }
        
        private EXMatch[] collectArguments() {
            int len = ruleIndex;
            assert len == rule.getLength() : 
                   "Should match entire rule";
            if (len == 0) return null;
            EXMatch[] args = new EXMatch[len];
            fillArgs(args);
            return args;
        }
        
        private void fillArgs(EXMatch[] args) {
            if (ruleIndex == 0) return;
            lastState.fillArgs(args);
            args[ruleIndex-1] = lastMatch;
        }

        private int origin(EXMatch[] args, int index) {
            if (args == null || args.length == 0) return index;
            return args[0].getStart();
        }
        
        protected boolean acceptNext(EXMatch match) {
            return nextPriority() <= match.getPriority() &&
                    nextSymbol().equals(match.getSymbol());
        }

        protected RuleState next(EXMatch match) {
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
