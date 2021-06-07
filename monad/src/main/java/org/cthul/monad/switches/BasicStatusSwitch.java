package org.cthul.monad.switches;

import java.util.function.Function;
import org.cthul.monad.Status;

public class BasicStatusSwitch<T, U, R> 
        extends SwitchExtension<Status, T, U, R,
                        BasicSwitch.Status<T, U, R>, 
                        Switch.Case<T, U, BasicSwitch.Status<T, U, R>>, 
                        Switch.Case<T, U, R>> 
        implements BasicSwitch.Status<T, U, R> {

    public static <T, U, R> Function<Switch<Status, T, U, R, ?, ?, ?>, BasicSwitch.Status<T, U, R>> wrapper() {
        return (Function) WRAPPER;
    }

    public static <T, U, R> BasicSwitch.Status<T, U, R> asBasicStatusSwitch(Switch<Status, T, U, R, ?, ?, ?> delegate) {
        return delegate.asStatusSwitch(wrapper());
    }

    public static <T, R> Function<Switch<Status, T, T, R, ?, ?, ?>, BasicSwitch.StatusIdentity<T, R>> identityWrapper() {
        return (Function) IDENTITY_WRAPPER;
    }

    public static <T, R> BasicSwitch.StatusIdentity<T, R> asBasicStatusIdentitySwitch(Switch<Status, T, T, R, ?, ?, ?> delegate) {
        return delegate.asStatusIdentitySwitch(identityWrapper());
    }
    
    public BasicStatusSwitch(Switch<Status, T, U, R, ?, ?, ?> delegate) {
        super(delegate);
    }

    @Override
    protected <R0, R1> Case<T, U, R1> newCaseImplementation(Case<T, U, R0> caseDelegate, Function<? super R0, ? extends R1> resultMapping) {
        return new CaseDelegator<>(caseDelegate, resultMapping);
    }

    @Override
    protected BasicSwitch.Status<T, U, R> wrapNextStep(Switch<Status, T, U, R, ?, ?, ?> newDelegate) {
        return newDelegate.asStatusSwitch(wrapper());
    }
    
    public static class Identity<T, R>
            extends SwitchExtension<Status, T, T, R, 
                            BasicSwitch.StatusIdentity<T, R>, 
                            Switch.IdentityCase<T, BasicSwitch.StatusIdentity<T, R>>, 
                            Switch.IdentityCase<T, R>>
            implements BasicSwitch.StatusIdentity<T, R>,
                    StatusSwitch.Identity<T, R,
                            BasicSwitch.StatusIdentity<T, R>, 
                            Switch.IdentityCase<T, BasicSwitch.StatusIdentity<T, R>>, 
                            Switch.IdentityCase<T, R>> {

        public Identity(Switch<Status, T, T, R, ?, ?, ?> delegate) {
            super(delegate);
        }

        @Override
        protected <R0, R1> Case<T, T, R1> newCaseImplementation(Case<T, T, R0> caseDelegate, Function<? super R0, ? extends R1> resultMapping) {
            return new BasicIdentitySwitch.IdentityCaseDelegator<>(caseDelegate, resultMapping);
        }

        @Override
        protected BasicSwitch.StatusIdentity<T, R> wrapNextStep(Switch<Status, T, T, R, ?, ?, ?> newDelegate) {
            return new BasicStatusSwitch.Identity<>(newDelegate);
        }
    }
    
    public static class Direct<T, U>
            extends SwitchExtension.Direct<Status, T, U, U,
                            BasicSwitch.Status<T, U, U>, 
                            Switch.Case<T, U, BasicSwitch.Status<T, U, U>>, 
                            Switch.Case<T, U, U>>
            implements StatusSwitch.Direct<T, U,
                            BasicSwitch.Status<T, U, U>, 
                            Switch.Case<T, U, BasicSwitch.Status<T, U, U>>, 
                            Switch.Case<T, U, U>> {

        public Direct(Switch<Status, T, U, U, ?, ?, ?> delegate) {
            super(delegate);
        }

        @Override
        protected <R0, R1> Case<T, U, R1> newCaseImplementation(Case<T, U, R0> caseDelegate, Function<? super R0, ? extends R1> resultMapping) {
            return new CaseDelegator<>(caseDelegate, resultMapping);
        }

        @Override
        protected BasicSwitch.Status<T, U, U> wrapNextStep(Switch<Status, T, U, U, ?, ?, ?> newDelegate) {
            return newDelegate.asStatusSwitch(wrapper());
        }
    }
    
    public static class DirectIdentity<T, U>
            extends SwitchExtension.Direct<Status, T, T, T,
                            BasicSwitch.StatusIdentity<T, T>, 
                            Switch.IdentityCase<T, BasicSwitch.StatusIdentity<T, T>>, 
                            Switch.IdentityCase<T, T>>
            implements StatusSwitch.DirectIdentity<T,
                            BasicSwitch.StatusIdentity<T, T>, 
                            Switch.IdentityCase<T, BasicSwitch.StatusIdentity<T, T>>, 
                            Switch.IdentityCase<T, T>> {

        public DirectIdentity(Switch<Status, T, T, T, ?, ?, ?> delegate) {
            super(delegate);
        }

        @Override
        protected <R0, R1> Switch.IdentityCase<T, R1> newCaseImplementation(Switch.Case<T, T, R0> caseDelegate, Function<? super R0, ? extends R1> resultMapping) {
            return new BasicIdentitySwitch.IdentityCaseDelegator<>(caseDelegate, resultMapping);
        }

        @Override
        protected BasicSwitch.StatusIdentity<T, T> wrapNextStep(Switch<Status, T, T, T, ?, ?, ?> newDelegate) {
            return newDelegate.asStatusIdentitySwitch(identityWrapper());
        }
    }
    
    private static final Function<Switch<Status,?,?,?,?,?,?>, ?> WRAPPER = BasicStatusSwitch::new;
    private static final Function<Switch<Status,Object,Object,?,?,?,?>, ?> IDENTITY_WRAPPER = BasicStatusSwitch.Identity::new;
}
