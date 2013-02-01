package org.cthul.parser.grammar;

import java.util.Collection;
import org.cthul.parser.NoMatchException;
import org.cthul.parser.util.Format;


public class ParseException extends NoMatchException {
    
    private final Collection<?> actual;
    private final Collection<?> expected;

    public ParseException(Collection<?> actual, Collection<?> expected, String input, int position, String message, Throwable cause) {
        super(input, position, message, cause);
        this.actual = actual;
        this.expected = expected;
    }

    @Override
    protected void msgLocation(StringBuilder sb) {
        super.msgLocation(sb);
        if (actual != null && ! actual.isEmpty()) {
            sb.append("\nGot\n");
            sb.append(Format.join("  ", "\n  ", "", actual));
        }
        if (expected != null && ! expected.isEmpty()) {
            sb.append("\nExpected\n");
            sb.append(Format.join("  ", "\n  ", "", expected));
        }
    }
    
    
}
