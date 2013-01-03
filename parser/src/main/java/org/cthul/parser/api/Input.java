package org.cthul.parser.api;

public abstract class Input<Token> {
    
    public abstract int getLength();
    
    public abstract Token get(int i);
    
}
