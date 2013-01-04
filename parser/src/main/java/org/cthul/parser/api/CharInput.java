package org.cthul.parser.api;

public abstract class CharInput extends Input<Character> {
    
    public abstract CharSequence getString();
    
    public abstract char getChar(int i);
    
}
