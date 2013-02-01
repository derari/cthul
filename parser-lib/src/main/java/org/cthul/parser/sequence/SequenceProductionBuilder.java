package org.cthul.parser.sequence;

import java.util.Map;
import java.util.WeakHashMap;
import org.cthul.parser.ProductionBuilder;
import org.cthul.parser.api.Context;
import org.cthul.parser.api.Match;
import org.cthul.parser.api.RuleKey;
import org.cthul.parser.grammar.api.ProductionMatch;
import org.cthul.parser.grammar.api.ProxyRuleEval;
import org.cthul.parser.grammar.api.RuleEval;

public class SequenceProductionBuilder {
    
    protected final SequenceBuilder<?,?> sequence;
    protected final RuleKey[] itemProduction;
    protected final RuleKey[] sepProduction;
    protected final boolean hasSeparator;
    protected final boolean includeSeparator;
    protected final boolean flattenItem;
    protected final boolean flattenSep;
    protected final boolean allowEmpty;
    protected final int minSize;
    protected final int stepSize;
    protected final RESet reSet;

    public SequenceProductionBuilder(SequenceBuilder<?, ?> sequence, RuleKey[] itemProduction, RuleKey[] sepProduction, boolean flatten, boolean includeSeparator, boolean allowEmpty, int minSize, int stepSize) {
        if (sequence == null) throw new NullPointerException("sequence");
        if (itemProduction == null || itemProduction.length == 0) {
            throw new IllegalArgumentException("Item production is empty");
        }
        this.sequence = sequence;
        this.itemProduction = itemProduction;
        this.sepProduction = sepProduction;
        this.hasSeparator = sepProduction != null && sepProduction.length > 0;
        if (includeSeparator && !hasSeparator) {
            throw new IllegalArgumentException(
                    "Cannot include separator: production is empty");
        }
        this.includeSeparator = includeSeparator;
        this.flattenItem = flatten;
        this.flattenSep = flatten && hasSeparator;
        if (minSize == 0) {
            if (!allowEmpty) {
                throw new IllegalArgumentException(
                        "Min size is zero, but empty not allowed");
            }
            minSize = 1;
        }
        this.allowEmpty = allowEmpty;
        this.minSize = minSize;
        if (stepSize < 1) {
            throw new IllegalArgumentException(
                    "Step size must be positive, but was " + stepSize);
        }
        this.stepSize = stepSize;
        this.reSet = getRESet(sequence);
    }
    
    public RuleKey buildLeftDeep(ProductionBuilder mainRule) {
        if (allowEmpty) {
            mainRule.reset();
            mainRule.setEval(reEmpty()).createProduction();
        }
        
        RuleKey seqKey = buildLeftDeepSequence(mainRule);
        mainRule.reset();
        mainRule.add(seqKey).setEval(ProxyRuleEval.instance());
        return mainRule.createProduction();
    }
    
    protected RuleKey buildLeftDeepSequence(ProductionBuilder mainRule) {
        ProductionBuilder stepRule = mainRule.newSubRule("step");
        RuleKey rkItem = buildItemProduction(mainRule);
        RuleKey rkSep = buildSepProduction(mainRule);
        
        // initial
        stepRule.reset().setEval(reInitial());
        addInitial(stepRule, rkItem, rkSep);
        stepRule.createProduction();
        
        // step
        stepRule.reset().setEval(reStep());
        stepRule.add(stepRule.getKey());
        addStep(stepRule, rkItem, rkSep);
        return stepRule.createProduction();
    }
    
    protected RuleKey buildItemProduction(ProductionBuilder owner) {
        if (itemProduction.length == 1) {
            return itemProduction[0];
        }
        ProductionBuilder rbItem = owner.newSubRule("item");
        addAll(rbItem, itemProduction);
        return rbItem.setEval(reArrayProxy()).createProduction();
    }
    
    protected RuleKey buildSepProduction(ProductionBuilder owner) {
        if (!hasSeparator) {
            return null;
        }
        if (sepProduction.length == 1) {
            return sepProduction[0];
        }
        ProductionBuilder rbItem = owner.newSubRule("separator");
        addAll(rbItem, sepProduction);
        return rbItem.setEval(reSepEval()).createProduction();
    }
    
    protected void addStep(ProductionBuilder pb, RuleKey rkItem, RuleKey rkSep) {
        for (int i = 0; i < stepSize; i++) {
            if (rkSep != null ) pb.add(rkSep);
            pb.add(rkItem);
        }
    }
    
    protected void addInitial(ProductionBuilder pb, RuleKey rkItem, RuleKey rkSep) {
        for (int i = 0; i < minSize; i++) {
            if (i > 0 && rkSep != null ) pb.add(rkSep);
            pb.add(rkItem);
        }
    }
    
    protected void addAll(ProductionBuilder pb, RuleKey... keys) {
        for (RuleKey rk: keys) pb.add(rk);
    }
    
    protected RuleEval reEmpty() {
        return reSet.reEmpty;
    }
    
    protected RuleEval reInitial() {
        return reSet.reInitial(flattenItem, flattenSep, hasSeparator, includeSeparator);
    }
    
    protected RuleEval reStep() {
        return reSet.reLeftStep(flattenItem, flattenSep, hasSeparator, includeSeparator);
    }
    
    protected RuleEval reArrayProxy() {
        return ARRAY_PROXY;
    }
    
    protected RuleEval reSepEval() {
        return SEP_EVAL;
    }
    
    protected static synchronized RESet getRESet(SequenceBuilder<?,?> seq) {
        RESet reSet = reSets.get(seq);
        if (reSet == null) {
            reSet = new RESet(seq);
            reSets.put(seq, reSet);
        }
        return reSet;
    }
    
    protected static final Map<SequenceBuilder<?,?>, RESet> reSets = new WeakHashMap<>();
    
    protected static class RESet {
        protected final SequenceBuilder seq;
        protected final REEmpty reEmpty;
        protected final REInitial[] reInitial;
        protected final RELeftStep[] reStep;

        @SuppressWarnings({"unchecked", "OverridableMethodCallInConstructor"})
        public RESet(SequenceBuilder seq) {
            this.seq = seq;
            reEmpty = new REEmpty(seq);
            reInitial = new REInitial[len()];
            reStep = new RELeftStep[len()];
        }
        
        protected int len() {
            return 8;
        }
        
        protected REInitial reInitial(boolean flattenItem, boolean flattenSep, boolean hasSep, boolean includeSep) {
            int id = id(flattenItem, flattenSep, hasSep, includeSep);
            REInitial re = reInitial[id];
            if (re == null) {
                re = new REInitial(seq, flattenItem, flattenSep, hasSep, includeSep);
                reInitial[id] = re;
            }
            return re;
        }
        
        protected RELeftStep reLeftStep(boolean flattenItem, boolean flattenSep, boolean hasSep, boolean includeSep) {
            int id = id(flattenItem, flattenSep, hasSep, includeSep);
            RELeftStep re = reStep[id];
            if (re == null) {
                re = new RELeftStep(seq, flattenItem, flattenSep, hasSep, includeSep);
                reStep[id] = re;
            }
            return re;
        }
        
        protected final int id(boolean flattenItem, boolean flattenSep, boolean hasSep, boolean includeSep) {
            return (hasSep ? 1 : 0) + (includeSep ? 1 : 0) + (flattenSep ? 1 : 0)
                    + (flattenItem ? 4 : 0);
                    
        }
    }
    
    protected static class REEmpty implements RuleEval {
        protected final SequenceBuilder<Object, Object> seq;
        public REEmpty(SequenceBuilder<Object, Object> seq) {
            this.seq = seq;
        }
        @Override
        public Object eval(Context<?> context, ProductionMatch match, Object arg) {
            return seq.newInstance();
        }
    }
    
    protected abstract static class REBase implements RuleEval {
        protected final SequenceBuilder<Object, Object> seq;
        protected final boolean flattenItem;
        protected final boolean flattenSep;
        protected final boolean hasSep;
        protected final boolean includeSep;
        public REBase(SequenceBuilder<Object, Object> seq, boolean flattenItem, boolean flattenSep, boolean hasSep, boolean includeSep) {
            this.seq = seq;
            this.flattenItem = flattenItem;
            this.flattenSep = flattenSep;
            this.hasSep = hasSep;
            this.includeSep = includeSep;
        }

        protected void addSteps(final Match<?>[] matches, Object arg, final Object sequence, int initial) {
            if (hasSep && !includeSep) {
                for (int i = initial; i < matches.length; i++) {
                    boolean isItem = i % 2 == 0;
                    if (isItem) {
                        Object item = matches[i].eval(arg);
                        add(isItem, sequence, item);
                    }
                }
            } else {
                for (int i = initial; i < matches.length; i++) {
                    Object item = matches[i].eval(arg);
                    add(true, sequence, item);
                }
            }
        }

        protected void add(boolean isItem, Object sequence, Object value) {
            assert isItem | includeSep : "Should be item or included separator";
            if (isItem ? flattenItem : flattenSep) {
                if (!(value instanceof Object[])) {
                    throw new IllegalArgumentException(
                            "Cannot flatten " + value);
                }
                Object[] item = (Object[]) value;
                for (Object o: item) seq.add(sequence, o);
            } else {
                seq.add(sequence, value);
            }
        }
    }
    
    protected static class REInitial extends REBase {
        public REInitial(SequenceBuilder<Object, Object> seq, boolean flattenItem, boolean flattenSep, boolean hasSep, boolean includeSep) {
            super(seq, flattenItem, flattenSep, hasSep, includeSep);
        }
        @Override
        public Object eval(Context<?> context, ProductionMatch match, Object arg) {
            final Match<?>[] matches = match.matches();
            final Object sequence = seq.newInstance();
            addSteps(matches, arg, sequence, 0);
            return sequence;
        }
    }
    
    protected static class RELeftStep extends REBase {
        public RELeftStep(SequenceBuilder<Object, Object> seq, boolean flattenItem, boolean flattenSep, boolean hasSep, boolean includeSep) {
            super(seq, flattenItem, flattenSep, hasSep, includeSep);
        }
        @Override
        public Object eval(Context<?> context, ProductionMatch match, Object arg) {
            final Match<?>[] matches = match.matches();
            final Object sequence = matches[0].eval(arg);
            addSteps(matches, arg, sequence, 1);
            return sequence;
        }
    }
    
    protected static final RuleEval ARRAY_PROXY = new RuleEval() {
        @Override
        public Object eval(Context<?> context, ProductionMatch match, Object arg) {
            final Match<?>[] matches = match.matches();
            final Object[] result = new Object[matches.length];
            for (int i = 0; i < result.length; i++) {
                result[i] = matches[i].eval(arg);
            }
            return result;
        }
    };
    
    protected static final RuleEval SEP_EVAL = new RuleEval() {
        @Override
        public Object eval(Context<?> context, ProductionMatch match, Object arg) {
            throw new UnsupportedOperationException("Evaluating separator");
        }
    };
    
}
