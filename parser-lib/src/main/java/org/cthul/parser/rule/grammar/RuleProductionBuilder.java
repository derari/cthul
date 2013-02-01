package org.cthul.parser.rule.grammar;

import java.util.ArrayList;
import java.util.List;
import org.cthul.parser.ProductionBuilder;
import org.cthul.parser.api.Context;
import org.cthul.parser.api.Match;
import org.cthul.parser.api.RuleKey;
import org.cthul.parser.grammar.api.NullRuleEval;
import org.cthul.parser.grammar.api.ProductionMatch;
import org.cthul.parser.grammar.api.RuleEval;
import org.cthul.parser.rule.*;

public abstract class RuleProductionBuilder {
    
    private final List<RuleKey> keys = new ArrayList<>();
    protected int matchCounter = -1;
    protected ProductionBuilder baseRule;
    protected int noResultsDepth = 0;
    
    public RuleKey buildRule(Rule rule, ProductionBuilder pb) {
        keys.clear();
        matchCounter = 0;
        baseRule = pb;
        return build(rule, pb).getOriginal();
    }
    
    protected InternRuleKey buildSubrule(Rule rule, ProductionBuilder pb) {
        ProductionBuilder sub = pb.newSubRule();
        return build(rule, sub);
    }
    
    protected InternRuleKey build(Rule rule, ProductionBuilder pb) {
        if (rule instanceof KeyMatchRule) {
            return build((KeyMatchRule) rule, pb);
        } else if (rule instanceof ProductionRule) {
            return build((ProductionRule) rule, pb);
        } else if (rule instanceof LookAheadRule) {
            return build((LookAheadRule) rule, pb);
        } else if (rule instanceof AntiMatchRule) {
            return build((AntiMatchRule) rule, pb);
        } else if (rule instanceof NoResultRule) {
            return build((NoResultRule) rule, pb);
        } else {
            throw new IllegalArgumentException("Unexpected rule: " + rule);
        }
    }
    
    protected InternRuleKey build(KeyMatchRule rule, ProductionBuilder pb) {
        if (noResultsDepth == 0) {
            RuleEval eval = PUT_MATCHES(matchCounter);
            assert matchCounter == keys.size();
            matchCounter++;
            keys.add(rule.getKey());
            setEval(pb, eval);
        } else {
            setEval(pb, NO_EVAL);
        }
        if (pb != baseRule) {
            return irk(rule.getKey(), -1);
        } else {
            pb.add(rule.getKey());
            return irk(pb.createProduction());
        }
    }
    
    protected InternRuleKey build(ProductionRule rule, ProductionBuilder pb) {
        int c = matchCounter;
        InternRuleKey[] irks = addSubrules(rule.getRules(), pb);
        setEval(pb, COLLECT_NESTED(c, irks));
        return irk(pb.createProduction());
    }
    
    protected InternRuleKey build(LookAheadRule rule, ProductionBuilder pb) {
        int c = matchCounter;
        InternRuleKey[] irks = addSubrules(rule.getRules(), pb);
        setEval(pb, COLLECT_NESTED(c, irks));
        return irk(pb.createLookAhead());
    }
    
    protected InternRuleKey build(AntiMatchRule rule, ProductionBuilder pb) {
        noResultsDepth++;
        addSubrules(rule.getRules(), pb);
        noResultsDepth--;
        setEval(pb, NullRuleEval.instance());
        return irk(pb.createAntiMatch());
    }
    
    protected InternRuleKey build(NoResultRule rule, ProductionBuilder pb) {
        noResultsDepth++;
        addSubrules(rule.getRules(), pb);
        noResultsDepth--;
        setEval(pb, NullRuleEval.instance());
        return irk(pb.createProduction());
    }
    
    protected InternRuleKey[] addSubrules(Iterable<Rule> rules, ProductionBuilder pb) {
        List<InternRuleKey> result = new ArrayList<>();
        for (Rule r: rules) {
            InternRuleKey irk = buildSubrule(r, pb);
            pb.add(irk.getOriginal());
            result.add(irk);
        }
        return result.toArray(new InternRuleKey[result.size()]);
    }
    
    protected void setEval(ProductionBuilder pb, RuleEval eval) {
        if (pb == baseRule) {
            int length = keys.size();
            eval = baseEval(new RootEval(length, eval));
        }
        pb.setEval(eval);
    }

    protected List<RuleKey> getKeys() {
        return keys;
    }
    
    protected abstract RuleEval baseEval(RuleProductionEval root);
    
    protected InternRuleKey irk(RuleKey rk) {
        return new InternRuleKey(rk, matchCounter);
    }
    
    protected InternRuleKey irk(RuleKey rk, int c) {
        return new InternRuleKey(rk, c);
    }
    
    protected static class InternRuleKey {

        /**
         * The next empty match index,
         * or -1 if this rule directly matches.
         */
        private final int matchCount;
        private final RuleKey original;
        
        public InternRuleKey(RuleKey rk, int matchCount) {
            this.original = rk;
            this.matchCount = matchCount;
        }

        public int getMatchCount() {
            return matchCount;
        }

        public RuleKey getOriginal() {
            return original;
        }
    }

    public static interface RuleProductionEval extends RuleEval {
        @Override
        Match<?>[] eval(Context<?> context, ProductionMatch match, Object arg);
    }
    
    protected static class RootEval implements RuleProductionEval {
        protected final int length;
        protected final RuleEval root;

        public RootEval(int length, RuleEval root) {
            this.length = length;
            this.root = root;
        }

        @Override
        public Match<?>[] eval(Context<?> context, ProductionMatch match, Object arg) {
            if (arg != null) throw new IllegalArgumentException(
                    "Argument not allowed: " + arg);
            Match<?>[] result = new Match<?>[length];
            root.eval(context, match, result);
            return result;
        }
    }
    
    protected static CollectNestedMatchesEval COLLECT_NESTED(int initialIndex, InternRuleKey[] irks) {
        final int[] indices = new int[irks.length];
        int nextIndex = initialIndex;
        for (int i = 0; i < indices.length; i++) {
            int irkIndex = irks[i].getMatchCount();
            if (irkIndex < 0) {
                // this rule does not fill the result array
                indices[i] = nextIndex;
                nextIndex++;
            } else {
                // this rule fills the result array, stopping at irkIndex
                indices[i] = -1;
                nextIndex = irkIndex;
            }
        }
        return new CollectNestedMatchesEval(indices);
    }
    
    protected static class CollectNestedMatchesEval implements RuleEval {
        
        private final int[] indices;

        public CollectNestedMatchesEval(int[] indices) {
            this.indices = indices;
        }
        
        @Override
        public Object eval(Context<?> context, ProductionMatch match, Object arg) {
            final Match<?>[] result = (Match<?>[]) arg;
            final Match<?>[] matches = match.matches();
            for (int i = 0; i < matches.length; i++) {
                Match<?> m = matches[i];
                int index = indices[i];
                if (index < 0) {
                    m.eval(arg);
                } else {
                    result[index] = m;
                }
            }
            return result;
        }
    }
    
    protected static PutMatchesEval PUT_MATCHES(int index) {
        if (index >= PUT_MATCHES.length) return new PutMatchesEval(index);
        PutMatchesEval pme = PUT_MATCHES[index];
        if (pme == null) {
            pme = new PutMatchesEval(index);
            PUT_MATCHES[index] = pme;
        }
        return pme;
    }
    
    private static final PutMatchesEval[] PUT_MATCHES = new PutMatchesEval[32];
    
    protected static class PutMatchesEval implements RuleEval {
        
        private final int index;

        public PutMatchesEval(int index) {
            this.index = index;
        }

        @Override
        public Object eval(Context<?> context, ProductionMatch match, Object arg) {
            Match<?>[] result = (Match<?>[]) arg;
            int i = index;
            for (Match<?> m: match.matches()) {
                result[i] = m;
                i++;
            }
            return result;
        }
    }
    
    protected static final RuleEval NO_EVAL = new RuleEval() {
        @Override
        public Object eval(Context<?> context, ProductionMatch match, Object arg) {
            throw new UnsupportedOperationException();
        }
    };
    
}
