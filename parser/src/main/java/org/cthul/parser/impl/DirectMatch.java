package org.cthul.parser.impl;

import org.cthul.parser.Context;
import org.cthul.parser.NoMatchException;
import org.cthul.parser.Token;
import org.cthul.parser.TokenBuilder;
import org.cthul.parser.lexer.AbstractMatch;

/**
 *
 * @author Arian Treffer
 */
public class DirectMatch extends AbstractMatch {

    public DirectMatch(String pattern) {
        super(pattern);
    }
    
    @Override
    protected Token<?> eval(TokenBuilder tokenBuilder, Context context) throws NoMatchException {
        return tokenBuilder.newToken();
    }
    
}
