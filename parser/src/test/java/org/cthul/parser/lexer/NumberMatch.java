package org.cthul.parser.lexer;

import org.cthul.parser.Context;
import org.cthul.parser.TokenBuilder;

/**
 *
 * @author Arian Treffer
 */
class NumberMatch extends AbstractMatch {
    
    public static final Match INSTANCE = new NumberMatch();

    public NumberMatch() {
        super("[\\d]+");
    }

    @Override
    protected Object eval(TokenBuilder tokenBuilder, Context context) {
        tokenBuilder.setKey("INT");
        return true;
    }
    
}
