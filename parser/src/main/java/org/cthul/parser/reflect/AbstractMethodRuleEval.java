package org.cthul.parser.reflect;

import java.lang.reflect.Method;
import org.cthul.parser.api.Context;
import org.cthul.parser.grammar.api.ProductionMatch;
import org.cthul.parser.grammar.api.RuleEval;
import org.cthul.parser.reflect.MethodExecutor.ArgMapFactory;

public class AbstractMethodRuleEval implements RuleEval {
    
    protected final MethodExecutor<? super ProductionMatch> exec;

    protected AbstractMethodRuleEval(MethodExecutor<? super ProductionMatch> exec) {
        this.exec = exec;
    }

    public <Hint> AbstractMethodRuleEval(Object impl, Method m, ArgMapFactory<? super ProductionMatch, ? super Hint> argMapFactory, Hint hint) {
        this(new MethodExecutor<>(impl, m, argMapFactory, hint));
    }

    @Override
    public Object eval(Context<?> context, ProductionMatch match, Object arg) {
        return exec.invoke(context, match, arg);
    }
    
}
