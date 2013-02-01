package org.cthul.parser.api;

import java.util.Iterator;

public abstract class Input<Token> implements Iterable<Token> {

    public abstract int getLength();

    public abstract Token get(int i);

    public abstract int startPosition(int i);

    public abstract int endPosition(int i);

    public abstract String asString();
    
    @Override
    public Iterator<Token> iterator() {
        return new Iterator<Token>() {
            private final int len = getLength();
            private int i = 0;

            @Override
            public boolean hasNext() {
                return i < len;
            }

            @Override
            public Token next() {
                i++;
                return get(i - 1);
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
}
