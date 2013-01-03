package org.cthul.parser.api;

public abstract class CharInput extends Input<Character> {
    
    public abstract CharSequence getInput();
    
    public abstract char getChar(int i);
    
}
