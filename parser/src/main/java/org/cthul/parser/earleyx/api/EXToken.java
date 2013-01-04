package org.cthul.parser.earleyx.api;

import org.cthul.parser.api.Context;
import org.cthul.parser.api.Rule;
import org.cthul.parser.api.StringInput;

public class EXToken extends EXMatch {
    
    private final Context<? extends StringInput> context;


    public EXToken(Rule rule, Context<? extends StringInput> context, int inputStart, int inputEnd, EXMatch[] args) {
        super(rule, inputStart, inputEnd, args);
        this.context = context;
    }

    @Override
    public Object eval() {
        return token();
    }
    
    protected String token() {
        return context.getInput().getString().substring(getStart(), getEnd());
    }

}
