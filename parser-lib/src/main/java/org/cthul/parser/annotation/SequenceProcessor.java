package org.cthul.parser.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import org.cthul.parser.ParserBuilder;
import org.cthul.parser.ProductionBuilder;
import org.cthul.parser.annotation.scan.DefaultProcessorBase;
import org.cthul.parser.api.Context;
import org.cthul.parser.api.RuleKey;
import org.cthul.parser.grammar.api.ProductionMatch;
import org.cthul.parser.grammar.api.RuleEval;
import org.cthul.parser.reflect.FieldRuleEval;
import org.cthul.parser.reflect.MethodRuleEval;
import org.cthul.parser.rule.*;
import org.cthul.parser.rule.grammar.RuleProductionBuilder;
import org.cthul.parser.rule.grammar.RuleProductionBuilder.RuleProductionEval;
import org.cthul.parser.rule.grammar.WrappedRuleEval;
import org.cthul.parser.sequence.SequenceBuilder;
import org.cthul.parser.sequence.SequenceProductionBuilder;
import org.cthul.parser.util.Inject;
import org.cthul.parser.util.InstanceMap;

public class SequenceProcessor extends DefaultProcessorBase<Object, Object> {
    
    @Inject
    public InstanceMap iMap;

    @Override
    public void process(AnnotatedElement ae, Object impl, Annotation at, ParserBuilder<Object, ?> pb) {
        if (at instanceof Sequence) {
            process(ae, impl, (Sequence) at, pb);
        } else {
            for (Sequence s: ((Sequences) at).value()) {
                process(ae, impl, s, pb);
            }
        }
//        Sequence atSeq = (Sequence) at;
//        Key name = ae.getAnnotation(Key.class);
//        RuleProductionBuilder rpb = getRuleProductionBuilder(impl, ae);
//        for (String p: atP.value()) {
//            String key = selectName(name, ae, p, true);
//            int priority = selectPriority(ae);
//            String pattern = selectRule(p);
//            Rule r = new RuleParser(pattern, key, priority).parse();
//            rpb.buildRule(r, pb.productionBuilder(key, priority));
//        }
    }
    
    protected void process(AnnotatedElement ae, Object impl, Sequence at, ParserBuilder<Object, ?> pb) {
        Key name = ae.getAnnotation(Key.class);
        String key = selectName(name, ae, "", true);
        int priority = selectPriority(ae);
        ProductionBuilder mainRule = pb.productionBuilder(key, priority);
        
        String item = at.item();
        Rule itemRule = new RuleParser(item, key, priority).parse();
        RuleKey[] itemProduction = {subProduction(itemRule, mainRule.newSubRule("item"))};
        
        String sep = at.separator();
        RuleKey[] sepProduction = {};
        if (!sep.isEmpty()) {
            Rule sepRule = new RuleParser(sep, key, priority).parse();
            RuleKey rk = subProduction(sepRule, mainRule.newSubRule("sep"));
            sepProduction = new RuleKey[]{rk};
        }
        
        boolean flatten = at.flatten() || defaultFlatten(itemRule);
        boolean allowEmpty = at.allowEmpty();
        int minSize = at.minSteps();
        int stepSize = at.stepSize();
        
        SequenceBuilder<?,?> sb = getBuilder(at);
        RuleKey seqKey = new SequenceProductionBuilder(sb, itemProduction, sepProduction, flatten, false, allowEmpty, minSize, stepSize)
                .buildLeftDeep(mainRule.newSubRule("sequence"));
        
        if (ae instanceof Method) {
            Method m = (Method) ae;
            MethodRuleEval mre =  new MethodRuleEval(impl, m, Arrays.asList(seqKey));
            mainRule.setEval(mre);
        } else if (ae instanceof Field) {
            Field f = (Field) ae;
            FieldRuleEval fre = FieldRuleEval.create(impl, f);
            mainRule.setEval(fre);
        } else {
            throw new IllegalArgumentException("" + ae);
        }
        mainRule.add(seqKey).createProduction();
    }

    protected RuleKey subProduction(Rule r, ProductionBuilder newSubRule) {
        RuleProductionBuilder rpb = new ArrayResultRPB();
        return rpb.buildRule(r, newSubRule);
    }

    protected boolean defaultFlatten(Rule itemRule) {
        if (!(itemRule instanceof CompositeRule)) return true;
        if (itemRule instanceof AntiMatchRule) return true;
        CompositeRule cr = (CompositeRule) itemRule;
        return cr.getRules().size() == 1;
    }

    protected SequenceBuilder<?, ?> getBuilder(Sequence at) {
        return iMap.get(at.type());
    }

    protected static class ArrayResultRPB extends RuleProductionBuilder {

        @Override
        protected RuleEval baseEval(RuleProductionEval root) {
            RuleEval re = new RuleEval() {
                @Override
                public Object eval(Context<?> context, ProductionMatch match, Object arg) {
                    final org.cthul.parser.api.Match<?>[] matches = match.matches();
                    final Object[] result = new Object[matches.length];
                    for (int i = 0; i < result.length; i++) {
                        result[i] = matches[i].eval(arg);
                    }
                    return result;
                }
            };
            return new WrappedRuleEval(root, re);
        }
        
    }
    
}
