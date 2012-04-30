package org.cthul.parser.lexer;

import org.cthul.parser.TokenChannel;
import org.cthul.parser.Context;
import org.cthul.parser.TokenBuilder;

/**
 *
 * @author Arian Treffer
 */
public class WhitespaceMatch extends AbstractMatch {

    public static final Match INSTANCE = new WhitespaceMatch();
    
    public WhitespaceMatch() {
        super("[\\s]+");
    }

    @Override
    protected Object eval(TokenBuilder tokenBuilder, Context context) {
        tokenBuilder.setChannel(TokenChannel.Whitespace);
        return true;
    }
    
}
