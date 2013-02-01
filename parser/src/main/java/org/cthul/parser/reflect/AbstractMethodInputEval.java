package org.cthul.parser.reflect;

import java.lang.reflect.Method;
import org.cthul.parser.api.Context;
import org.cthul.parser.api.StringInput;
import org.cthul.parser.lexer.api.InputEval;
import org.cthul.parser.reflect.MethodExecutor.ArgMapFactory;

public class AbstractMethodInputEval<Token, Match> implements InputEval<Token, Match> {
    
    protected final MethodExecutor<? super Match> exec;

    protected AbstractMethodInputEval(MethodExecutor<? super Match> exec) {
        this.exec = exec;
    }

    public <Hint> AbstractMethodInputEval(Object impl, Method m, ArgMapFactory<? super Match, ? super Hint> argMapFactory, Hint hint) {
        this(new MethodExecutor<>(impl, m, argMapFactory, hint));
    }

    @Override
    @SuppressWarnings("unchecked")
    public Token eval(Context<? extends StringInput> context, Match match, int start, int end) {
        return (Token) exec.invoke(context, match, null);
    }
    
}
