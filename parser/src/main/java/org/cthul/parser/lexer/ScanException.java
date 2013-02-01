package org.cthul.parser.lexer;

import org.cthul.parser.NoMatchException;


public class ScanException extends NoMatchException {
    
    private final Object lastMatch;

    public ScanException(Object lastMatch, String input, int position, String message, Throwable cause) {
        super(input, position, message, cause);
        this.lastMatch = lastMatch;
    }

    @Override
    protected void msgLocation(StringBuilder sb) {
        super.msgLocation(sb);
        if (lastMatch != null) {
            sb.append("\nLast match was \"").append(str(lastMatch)).append('"');
        }
    }
    
    
}
