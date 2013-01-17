package org.cthul.parser.api;

public class StringInput extends CharInput {
    
    private final String input;

    public StringInput(String input) {
        this.input = input;
    }

    @Override
    public String getString() {
        return input;
    }

    @Override
    public char getChar(int i) {
        return input.charAt(i);
    }

    @Override
    public Character get(int i) {
        return input.charAt(i);
    }
    
    @Override
    public int getLength() {
        return input.length();
    }

    @Override
    public String toString() {
        return input;
    }
    
}
