package org.cthul.parser.grammar.api;

import org.cthul.parser.api.Context;
import org.cthul.parser.api.Match;

public class Redirect implements RuleEval {

    protected static final Redirect[] INSTANCES = new Redirect[32];
    
    public static Redirect instance() {
        return instance(0);
    }
    
    public static Redirect instance(int i) {
        if (i >= INSTANCES.length) return new Redirect(i);
        Redirect r = INSTANCES[i];
        if (r == null) {
            r = new Redirect(i);
            INSTANCES[i] = r;
        }
        return r;
    }
    
    public static Redirect step(int i) {
        return instance(i);
    }
    
    protected final int i;

    public Redirect(int i) {
        this.i = i;
    }
    
    @Override
    public Object eval(Context<?> context, ProductionMatch match, Object arg) {
        Match[] matches = match.matches();
        return matches[i].eval(arg);
    }
    
}
