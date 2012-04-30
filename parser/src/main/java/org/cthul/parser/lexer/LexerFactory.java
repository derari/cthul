package org.cthul.parser.lexer;

import java.util.List;
import org.cthul.parser.Lexer;

/**
 *
 * @author Arian Treffer
 */
public interface LexerFactory {
    
    public Lexer create(List<? extends Match> rules);
    
    public Lexer create(Match... rules);
    
}
