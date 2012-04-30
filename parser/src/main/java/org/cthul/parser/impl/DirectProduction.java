package org.cthul.parser.impl;

import org.cthul.parser.Context;
import org.cthul.parser.Value;
import org.cthul.parser.grammar.earley.AbstractProduction;

/**
 *
 * @author Arian Treffer
 */
public class DirectProduction extends AbstractProduction {

    public DirectProduction(String left, int priority, String right) {
        super(left, priority, right);
    }

    public DirectProduction(String left, int priority, String[] right, int[] priorities) {
        super(left, priority, right, priorities);
    }

    @Override
    public Object invoke(Context context, Value<?>... args) {
        if (args.length == 0) {
            return null;
        } else {
            return args[0].getValue();
        }
    }
    
}
