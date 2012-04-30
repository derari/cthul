package org.cthul.parser.grammar;

import org.cthul.parser.grammar.earley.Production;
import org.cthul.parser.grammar.earley.AbstractProduction;
import org.cthul.parser.Context;
import org.cthul.parser.Value;

/**
 *
 * @author Arian Treffer
 */
public class IntProduction extends AbstractProduction {

    public static Production INSTANCE = new IntProduction();
    
    public IntProduction() {
        super("term", 100, "INT");
    }

    @Override
    public Object invoke(Context context, Value<?>... args) {
        return Integer.parseInt((String) args[0].getValue());
    }
    
}
