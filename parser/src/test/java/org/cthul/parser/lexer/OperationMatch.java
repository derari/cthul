package org.cthul.parser.lexer;

import org.cthul.parser.Context;
import org.cthul.parser.TokenBuilder;

/**
 *
 * @author Arian Treffer
 */
public class OperationMatch extends AbstractMatch {
    
    public static final Match INSTANCE = new OperationMatch();

    public OperationMatch() {
        super("[+\\-*/]");
    }
    
    @Override
    protected Object eval(TokenBuilder tokenBuilder, Context context) {
        return true;
    }
    
}
