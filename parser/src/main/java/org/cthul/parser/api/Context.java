package org.cthul.parser.api;

import org.cthul.parser.util.InstanceMap;

public class Context<I extends Input<?>> extends InstanceMap {
    
    public static Context<StringInput> forString(String input) {
        return new Context<>(new StringInput(input));
    }
    
    private final I input;

    public Context(I input) {
        this.input = input;
        put(input.getClass(), input, Input.class, AnyInterface.class);
    }
    
    protected Context(Context<?> c, I input) {
        super(c);
        this.input = input;
        replace(input.getClass(), input, Input.class, AnyInterface.class);
    }

    @SuppressWarnings("unchecked")
    public <T extends Input<?>> Context<T> forInput(T input) {
        if (this.input == input) return (Context) this;
        return new Context<>(this, input);
    }
    
    public I getInput() {
        return input;
    }
    
}
