package org.cthul.monad.switches;

import java.util.function.Function;

public class BasicInterfaceSwitch<K, T, U, R> 
        extends SwitchExtension<K, T, U, R,
                        BasicSwitch<K, T, U, R>, 
                        Switch.Case<T, U, BasicSwitch<K, T, U, R>>, 
                        Switch.Case<T, U, R>> 
        implements BasicSwitch<K, T, U, R> {

    public static <K, T, U, R> Function<Switch<K, T, U, R, ?, ?, ?>, BasicSwitch<K, T, U, R>> wrapper() {
        return (Function) WRAPPER;
    }

    public static <K, T, U, R> BasicSwitch<K, T, U, R> asBasicSwitch(Switch<K, T, U, R, ?, ?, ?> delegate) {
        return delegate.asBasicSwitch();
    }
    
    public BasicInterfaceSwitch(Switch<K, T, U, R, ?, ?, ?> delegate) {
        super(delegate);
    }

    @Override
    protected <R0, R1> Case<T, U, R1> newCaseImplementation(Case<T, U, R0> caseDelegate, Function<? super R0, ? extends R1> resultMapping) {
        return new IdentityDelegator<>(caseDelegate, resultMapping);
    }

    @Override
    protected BasicSwitch<K, T, U, R> wrapNextStep(Switch<K, T, U, R, ?, ?, ?> newDelegate) {
        return newDelegate.asBasicSwitch();
    }
    
    private static final Function<Switch<?, ?, ?, ?, ?, ?, ?>, ?> WRAPPER = BasicInterfaceSwitch::new;
}
