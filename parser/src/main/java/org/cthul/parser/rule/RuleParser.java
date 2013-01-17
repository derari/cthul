package org.cthul.parser.rule;

/**
 * Syntax
 * Strings:     "abc", 'abc'
 * Keys:        abc
 * Choice:      A | B
 * Group:       ( A )
 * Look ahead:  &A
 * Anti match:  !A
 * Priority:    A! A. A~ A? A[1]
 */
public class RuleParser {
    
    protected static final char EOI = '\0';
    
    protected final String string;
    protected final int start;
    protected final int end;
    protected int position;

    public RuleParser(String string, int start, int end) {
        this.string = string;
        this.start = start;
        this.end = end;
    }
    
    public Rule parse() {
        position = start;
        return parseSubrule();
    }
    
    protected boolean atEnd() {
        return position >= end;
    }
    
    protected char charAt(int i) {
        if (i < start || i >= end) return EOI;
        char c = string.charAt(i);
        if (Character.isWhitespace(c)) return ' ';
        return c;
    }
    
    protected char current() {
        return charAt(position);
    }
    
    protected char currentAfterWS() {
        char c = current();
        while (c == ' ') c = next();
        return c;
    }
    
    protected char next() {
        if (atEnd()) return EOI;
        position++;
        return current();
    }
    
    protected char nextAfterWS() {
        next();
        return currentAfterWS();
    }
    
    protected char peek() {
        return charAt(position+1);
    }

    protected Rule parseSubrule() {
        switch (current()) {
            case '!': return parseAntiMatch();
            case '&': return parseLookAhead();
            case '(': return parseGroup();
        }
        return null;
    }

    protected Rule parseAntiMatch() {
        assert current() == '!';
        throw new UnsupportedOperationException("Not yet implemented");
    }

    protected Rule parseLookAhead() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    protected Rule parseGroup() {
        throw new UnsupportedOperationException("Not yet implemented");
    }
    
}
