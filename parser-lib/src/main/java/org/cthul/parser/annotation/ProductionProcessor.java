package org.cthul.parser.annotation;

import org.cthul.parser.annotation.scan.DefaultProcessorBase;
import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.AnnotatedElement;
import org.cthul.parser.ParserBuilder;
import org.cthul.parser.rule.Rule;
import org.cthul.parser.rule.RuleParser;
import org.cthul.parser.rule.grammar.ReflectiveRPB;
import org.cthul.parser.rule.grammar.RuleProductionBuilder;

public class ProductionProcessor extends DefaultProcessorBase<Object, Object> {

    @Override
    public void process(AnnotatedElement ae, Object impl, Annotation at, ParserBuilder<Object, ?> pb) {
        Production atP = (Production) at;
        Key name = ae.getAnnotation(Key.class);
        RuleProductionBuilder rpb = getRuleProductionBuilder(impl, ae);
        for (String p: atP.value()) {
            String key = selectName(name, ae, p, true);
            int priority = selectPriority(ae);
            String pattern = selectRule(p);
            Rule r = new RuleParser(pattern, key, priority).parse();
            rpb.buildRule(r, pb.productionBuilder(key, priority));
        }
    }

    protected RuleProductionBuilder getRuleProductionBuilder(Object impl, AnnotatedElement ae) {
        return new ReflectiveRPB(impl, (AccessibleObject) ae);
    }
    
    
}
