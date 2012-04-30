package org.cthul.parser.grammar.earley;

import org.cthul.parser.Context;
import org.cthul.parser.Value;

/**
 *
 * @author Arian Treffer
 */
public abstract class EmptyProduction extends AbstractProduction {

    public EmptyProduction(String left, int priority) {
        super(left, priority, "");
    }

    @Override
    public Object invoke(Context context, Value<?>... args) {
        return defaultValue(context);
    }
    
    protected abstract Object defaultValue(Context context);
    
}
