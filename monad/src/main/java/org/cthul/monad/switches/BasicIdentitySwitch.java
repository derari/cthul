package org.cthul.monad.switches;

import java.util.function.Function;

public class BasicIdentitySwitch<K, T, R>
        extends SwitchExtension<K, T, T, R, 
                        BasicSwitch.Identity<K, T, R>, 
                        Switch.IdentityCase<T, BasicSwitch.Identity<K, T, R>>, 
                        Switch.IdentityCase<T, R>>
        implements BasicSwitch.Identity<K, T, R> {
    
    public static <K, T, R> Function<Switch<K, T, T, R, ?, ?, ?>, BasicSwitch.Identity<K, T, R>> wrapper() {
        return (Function) WRAPPER;
    }
    
    public BasicIdentitySwitch(Switch<K, T, T, R, ?, ?, ?> delegate) {
        super(delegate);
    }

    @Override
    protected <R0, R1> Case<T, T, R1> newCaseImplementation(Case<T, T, R0> caseDelegate, Function<? super R0, ? extends R1> resultMapping) {
        return new IdentityCaseDelegator<>(caseDelegate, resultMapping);
    }

    @Override
    protected BasicSwitch.Identity<K, T, R> wrapNextStep(Switch<K, T, T, R, ?, ?, ?> newDelegate) {
        return newDelegate.asIdentitySwitch(wrapper());
    }
    
    protected static class IdentityCaseDelegator<T, R0, R1> 
            extends CaseDelegator<T, T, R0, R1>
            implements Switch.IdentityCase<T, R1> {

        public IdentityCaseDelegator(Case<T, T, R0> delegate, Function<? super R0, ? extends R1> resultMapping) {
            super(delegate, resultMapping);
        }
    }
    
    private static final Function<Switch<?,Object,Object,?,?,?,?>, ?> WRAPPER = BasicIdentitySwitch::new;
}
