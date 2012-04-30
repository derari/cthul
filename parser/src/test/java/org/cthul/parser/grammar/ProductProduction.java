package org.cthul.parser.grammar;

import org.cthul.parser.grammar.earley.Production;
import org.cthul.parser.grammar.earley.AbstractProduction;
import org.cthul.parser.Context;
import org.cthul.parser.Value;

/**
 *
 * @author Arian Treffer
 */
public class ProductProduction extends AbstractProduction {
    
    public static Production INSTANCE = new ProductProduction();

    public ProductProduction() {
        super("term", 10, "!term * term");
    }

    @Override
    public Object invoke(Context context, Value<?>... args) {
        Integer a = (Integer) args[0].getValue();
        Integer b = (Integer) args[2].getValue();
        return a * b;
    }
    
}
