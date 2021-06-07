package org.cthul.monad.switches;

import java.util.function.Function;
import org.cthul.monad.Status;
import org.cthul.monad.Unsafe;

public class BasicResultStatusSwitch<T, U, X extends Exception, R>
        extends SwitchExtension<Status, Unsafe<T, X>, Unsafe<U, X>, R, 
                BasicSwitch.ResultStatus<T, U, X, R>, 
                ResultSwitch.Case<T, U, X, BasicSwitch.ResultStatus<T, U, X, R>>, 
                ResultSwitch.Case<T, U, X, R>> 
        implements BasicSwitch.ResultStatus<T, U, X, R> {

    public BasicResultStatusSwitch(Switch<Status, Unsafe<T, X>, Unsafe<U, X>, R, ?, ?, ?> delegate) {
        super(delegate);
    }

    @Override
    protected <R0, R1> ResultSwitch.Case<T, U, X, R1> newCaseImplementation(Switch.Case<Unsafe<T, X>, Unsafe<U, X>, R0> caseDelegate, Function<? super R0, ? extends R1> resultMapping) {
        return new BasicResultSwitch.ResultCaseDelegator<>(caseDelegate, resultMapping);
    }

    @Override
    protected BasicSwitch.ResultStatus<T, U, X, R> wrapNextStep(Switch<Status, Unsafe<T, X>, Unsafe<U, X>, R, ?, ?, ?> newDelegate) {
        return new BasicResultStatusSwitch<>(newDelegate);
    }
    
    public static class Identity<T, X extends Exception, R>
            extends SwitchExtension<Status, Unsafe<T, X>, Unsafe<T, X>, R, 
                    BasicSwitch.ResultStatusIdentity<T, X, R>, 
                    ResultSwitch.IdentityCase<T, X, BasicSwitch.ResultStatusIdentity<T, X, R>>, 
                    ResultSwitch.IdentityCase<T, X, R>> 
            implements BasicSwitch.ResultStatusIdentity<T, X, R> {

        public Identity(Switch<Status, Unsafe<T, X>, Unsafe<T, X>, R, ?, ?, ?> delegate) {
            super(delegate);
        }

        @Override
        protected <R0, R1> ResultSwitch.IdentityCase<T, X, R1> newCaseImplementation(Case<Unsafe<T, X>, Unsafe<T, X>, R0> caseDelegate, Function<? super R0, ? extends R1> resultMapping) {
            return new BasicResultSwitch.ResultIdentityCaseDelegator<>(caseDelegate, resultMapping);
        }

        @Override
        protected BasicSwitch.ResultStatusIdentity<T, X, R> wrapNextStep(Switch<Status, Unsafe<T, X>, Unsafe<T, X>, R, ?, ?, ?> newDelegate) {
            return new BasicResultStatusSwitch.Identity<>(newDelegate);
        }
    }
    
    public static class Direct<T, U, X extends Exception>
            extends SwitchExtension.Direct<Status, Unsafe<T, X>, Unsafe<U, X>, Unsafe<U, X>, 
                            BasicSwitch.ResultStatus<T, U, X, Unsafe<U, X>>, 
                            ResultSwitch.Case<T, U, X, BasicSwitch.ResultStatus<T, U, X, Unsafe<U, X>>>, 
                            ResultSwitch.Case<T, U, X, Unsafe<U, X>>>
            implements BasicSwitch.DirectResultStatus<T, U, X> {

        public Direct(Switch<Status, Unsafe<T, X>, Unsafe<U, X>, Unsafe<U, X>, ?, ?, ?> delegate) {
            super(delegate);
        }

        @Override
        protected <R0, R1> Switch.Case<Unsafe<T, X>, Unsafe<U, X>, R1> newCaseImplementation(Switch.Case<Unsafe<T, X>, Unsafe<U, X>, R0> caseDelegate, Function<? super R0, ? extends R1> resultMapping) {
            return new BasicResultSwitch.ResultCaseDelegator<>(caseDelegate, resultMapping);
        }

        @Override
        protected BasicSwitch.ResultStatus<T, U, X, Unsafe<U, X>> wrapNextStep(Switch<Status, Unsafe<T, X>, Unsafe<U, X>, Unsafe<U, X>, ?, ?, ?> newDelegate) {
            return new BasicResultStatusSwitch<>(newDelegate);
        }
    }

    public static class DirectIdentity<T, X extends Exception>
            extends SwitchExtension.Direct<Status, Unsafe<T, X>, Unsafe<T, X>, Unsafe<T, X>,
                            BasicSwitch.ResultStatusIdentity<T, X, Unsafe<T, X>>, 
                            ResultSwitch.IdentityCase<T, X, BasicSwitch.ResultStatusIdentity<T, X, Unsafe<T, X>>>, 
                            ResultSwitch.IdentityCase<T, X, Unsafe<T, X>>>
            implements BasicSwitch.DirectResultStatusIdentity<T, X> {

        public DirectIdentity(Switch<Status, Unsafe<T, X>, Unsafe<T, X>, Unsafe<T, X>, ?, ?, ?> delegate) {
            super(delegate);
        }

        @Override
        protected <R0, R1> Switch.Case<Unsafe<T, X>, Unsafe<T, X>, R1> newCaseImplementation(Switch.Case<Unsafe<T, X>, Unsafe<T, X>, R0> caseDelegate, Function<? super R0, ? extends R1> resultMapping) {
            return new BasicResultSwitch.ResultIdentityCaseDelegator<>(caseDelegate, resultMapping);
        }

        @Override
        protected BasicSwitch.ResultStatusIdentity<T, X, Unsafe<T, X>> wrapNextStep(Switch<Status, Unsafe<T, X>, Unsafe<T, X>, Unsafe<T, X>, ?, ?, ?> newDelegate) {
            return new BasicResultStatusSwitch.Identity<>(newDelegate);
        }
    }
}
