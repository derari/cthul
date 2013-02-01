package org.cthul.parser.rule.grammar;

import org.cthul.parser.api.Context;
import org.cthul.parser.api.Match;
import org.cthul.parser.api.RuleKey;
import org.cthul.parser.grammar.api.ProductionMatch;
import org.cthul.parser.grammar.api.RuleEval;
import org.cthul.parser.rule.grammar.RuleProductionBuilder.RuleProductionEval;

/**
 * Evaluates a rule eval with the matches that were collected by a RuleProductionEval.
 */
public class WrappedRuleEval implements RuleEval {

    protected final RuleProductionEval internEval;
    protected final RuleEval wrappedEval;

    public WrappedRuleEval(RuleProductionEval internEval, RuleEval wrappedEval) {
        this.internEval = internEval;
        this.wrappedEval = wrappedEval;
    }

    @Override
    public Object eval(Context<?> context, ProductionMatch match, Object arg) {
        ProductionMatch wrappedMatch = new WrappedProductionMatch(internEval, context, match);
        return wrappedEval.eval(context, wrappedMatch, arg);
    }
    
    public static class WrappedProductionMatch implements ProductionMatch {
        
        protected final RuleProductionEval internEval;
        protected final Context<?> context;
        protected final ProductionMatch wrapped;
        protected Match<?>[] matches = null;

        public WrappedProductionMatch(RuleProductionEval internEval, Context<?> context, ProductionMatch wrapped) {
            this.internEval = internEval;
            this.context = context;
            this.wrapped = wrapped;
        }

        @Override
        public RuleKey getKey() {
            return wrapped.getKey();
        }

        @Override
        public int getInputStart() {
            return wrapped.getInputStart();
        }

        @Override
        public int getInputEnd() {
            return wrapped.getInputEnd();
        }

        @Override
        public int getStartIndex() {
            return wrapped.getStartIndex();
        }

        @Override
        public int getEndIndex() {
            return wrapped.getEndIndex();
        }

        @Override
        public Object eval(Object arg) {
            // calling this will likely cause an infinite recursion
            return wrapped.eval(arg);
        }

        @Override
        public Match<?>[] matches() {
            if (matches == null) {
                matches = internEval.eval(context, wrapped, null);
                assert matches != null;
            }
            return matches;
        }

        @Override
        public String getSymbol() {
            return wrapped.getSymbol();
        }

        @Override
        public int getPriority() {
            return wrapped.getPriority();
        }

        @Override
        public Object eval() {
            // calling this will likely cause an infinite recursion
            return wrapped.eval();
        }

        @Override
        public String toString() {
            return "[" + wrapped.toString() + "]";
        }
    }    
}
