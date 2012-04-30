package org.cthul.parser.lexer;

import org.cthul.parser.Context;
import org.cthul.parser.NoMatchException;
import org.cthul.parser.Token;
import org.cthul.parser.TokenBuilder;

/**
 *
 * @author Arian Treffer
 */
public interface Match {
    
    public String getPattern();
    
    public Token<?> invoke(TokenBuilder tokenBuilder, Context context) throws NoMatchException;
    
}
