package org.cthul.parser.lexer;

import org.cthul.parser.NoMatchException;
import org.cthul.parser.grammar.api.InputMatch;


public class LexerException extends NoMatchException {
    
    private final Object lastMatch;

    public LexerException(Object lastMatch, String input, int position, String message, Throwable cause) {
        super(input, position, message, cause);
        this.lastMatch = lastMatch;
    }

    @Override
    protected void msgLocation(StringBuilder sb) {
        super.msgLocation(sb);
        if (lastMatch != null) {
            sb.append("\nLast match was ").append(str(lastMatch));
        }
    }
    
    
}
