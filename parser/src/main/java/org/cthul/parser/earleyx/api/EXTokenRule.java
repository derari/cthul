package org.cthul.parser.earleyx.api;

import org.cthul.parser.api.Context;
import org.cthul.parser.api.StringInput;

public abstract class EXTokenRule<C extends Context<? extends StringInput>> extends EXRule<C> {

    public EXTokenRule(String symbol, int priority) {
        super(symbol, priority);
    }
    
    protected EXMatch token(C context, int start, int end) {
        return new EXToken(this, context, start, end, null);
    }

    @Override
    public String toString() {
        return super.toString() + " " + tokenString();
    }

    protected abstract String tokenString();
    
}
