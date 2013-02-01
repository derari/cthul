package org.cthul.parser.rule.grammar;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import org.cthul.parser.grammar.api.RuleEval;
import org.cthul.parser.reflect.FieldRuleEval;
import org.cthul.parser.reflect.MethodRuleEval;

public class ReflectiveRPB extends RuleProductionBuilder {

    protected final Object impl;
    protected final AccessibleObject ao;

    public ReflectiveRPB(Object impl, AccessibleObject ao) {
        this.impl = impl;
        this.ao = ao;
    }

    @Override
    protected RuleEval baseEval(RuleProductionEval root) {
        if (ao instanceof Method) {
            Method m = (Method) ao;
            MethodRuleEval mre =  new MethodRuleEval(impl, m, getKeys());
            return new WrappedRuleEval(root, mre);
        } else if (ao instanceof Field) {
            Field f = (Field) ao;
            FieldRuleEval fre = FieldRuleEval.create(impl, f);
            if (fre.isSimpleValue()) {
                // no need to eval root
                return fre;
            }
            return new WrappedRuleEval(root, fre);
        }
        throw new IllegalArgumentException("" + ao);
    }
    
}
