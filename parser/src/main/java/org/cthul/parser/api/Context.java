package org.cthul.parser.api;

public class Context<I extends Input<?>> {
    
    public static Context<StringInput> forString(String input) {
        return new Context<>(new StringInput(input));
    }
    
    private final I input;

    public Context(I input) {
        this.input = input;
    }
    
    public Context(Context<?> c, I input) {
        this.input = input;
    }

    public <T extends Input<?>> Context<T> forInput(T input) {
        if (this.input == input) return (Context) this;
        return new Context<>(this, input);
    }
    
    public I getInput() {
        return input;
    }
    
}
