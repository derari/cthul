package org.cthul.parser.lexer;

import java.util.Arrays;
import org.cthul.parser.Lexer;

/**
 *
 * @author Arian Treffer
 */
public abstract class AbstractLexerFactory implements LexerFactory {

    @Override
    public Lexer create(Match... rules) {
        return create(Arrays.asList(rules));
    }
    
}
