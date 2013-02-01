package org.cthul.parser.api;

public abstract class CharInput extends Input<Character> {
    
    public abstract CharSequence getString();
    
    public abstract char getChar(int i);

    @Override
    public String asString() {
        return getString().toString();
    }

    @Override
    public int startPosition(int i) {
        return i;
    }

    @Override
    public int endPosition(int i) {
        return i;
    }
    
}
