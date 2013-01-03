package org.cthul.parser.api;

public class Context<I extends Input<?>> {
    
    public static Context<StringInput> forString(String input) {
        return new Context<>(new StringInput(input));
    }
    
    private final I input;

    public Context(I input) {
        this.input = input;
    }

    public I getInput() {
        return input;
    }
    
}
