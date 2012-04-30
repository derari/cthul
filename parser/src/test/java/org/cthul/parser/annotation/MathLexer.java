package org.cthul.parser.annotation;

import org.cthul.parser.TokenBuilder;
import org.cthul.parser.impl.Inject;
import org.cthul.parser.lexer.IntegerToken;

/**
 *
 * @author Arian Treffer
 */
public class MathLexer {
    
    @Key
    @Match("[\\d]+")
    @TokenClass(IntegerToken.class)
    public static void INT() {}
    
    @Match("[+\\-*/()]")
    public static void op() {}
    
    @Key
    @Channel(Channel.Whitespace)
    @Match("[\\s]+")
    public static void WS() {}
}
