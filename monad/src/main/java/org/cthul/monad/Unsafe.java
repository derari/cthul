package org.cthul.monad;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import org.cthul.monad.function.CheckedFunction;
import org.cthul.monad.result.NoValue;
import org.cthul.monad.result.ResultMessage;
import org.cthul.monad.result.ValueResult;
import org.cthul.monad.switches.BasicResultSwitch;
import org.cthul.monad.switches.BasicSwitch;
import org.cthul.monad.switches.ResultChoices;
import org.cthul.monad.switches.ValueSwitch;

public interface Unsafe<T, X extends Exception> extends ResultChoices.Direct<T, X> {

    Scope getScope();

    Status getStatus();

    boolean isPresent();

    default boolean isException() {
        return !isPresent();
    }

    T get() throws X;

    X getException() throws ValueIsPresentException;

    NoValue<X> noValue() throws ValueIsPresentException;

    ValueResult<T> value() throws X;

    Result<T> unchecked();

    Unsafe<T, X> ifPresent(Consumer<? super T> consumer);

    Unsafe<T, X> ifException(Consumer<? super X> consumer);

    <U> Unsafe<U, X> flatMap(Function<? super T, ? extends Unsafe<U, ? extends X>> function);

    <X2 extends Exception> Unsafe<T, X2> flatMapException(Function<? super X, ? extends Unsafe<? extends T, X2>> function);

    <U, X2 extends Exception> Unsafe<U, X> map(CheckedFunction<? super T, ? extends U, X2> function) throws X2;

    <X2 extends Exception, X3 extends Exception> Unsafe<T, X2> mapException(CheckedFunction<? super X, ? extends X2, X3> function) throws X3;

    T orElse(T other);

    T orElseGet(Supplier<? extends T> other);

    T orElseApply(Function<? super Unsafe<?, X>, ? extends T> other);

    <X2 extends Exception> T orElseThrow(Supplier<? extends X2> exceptionSupplier) throws X2;

    @Override
    default BasicSwitch.DirectResultIdentity<Unsafe<T, X>, T, X> choose() {
        return BasicResultSwitch.wrapDirectIdentity(ValueSwitch.choose(this));
    }

    void logInfo(String string, Object... args);

    default ResultMessage asMessage() {
        return new ResultMessage(getScope().getName(), getStatus(), toString());
    }
}
