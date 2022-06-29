package org.cthul.monad.result;

import org.cthul.monad.DefaultStatus;
import org.cthul.monad.result.OkValue.AnonymousScope;
import org.cthul.monad.util.ScopedExceptionType;

@SuppressWarnings("LeakingThisInConstructor")
public class CheckedScope<X extends Exception & NoValue<X>> extends NamedScope {

    private final ScopedExceptionType<X> checked;

    public CheckedScope(ScopedExceptionType<X> type) {
        super(type.getExceptionClass());
        this.checked = type;
    }

    public CheckedScope(ScopedExceptionType.Factory<X> factory) {
        this(true, (Class<X>) factory.newException(AnonymousScope.INSTANCE, DefaultStatus.NO_VALUE, null, null).getClass(), factory);
    }

    private CheckedScope(boolean internal, Class<X> clazz, ScopedExceptionType.Factory<X> factory) {
        super(clazz);
        assert internal;
        this.checked = ScopedExceptionType.build(this, clazz, factory);
    }

    public CheckedScope(Class<?> clazz, ScopedExceptionType.Factory<X> factory) {
        super(clazz);
        this.checked = ScopedExceptionType.build(this, factory);
    }

    public CheckedScope(String name, ScopedExceptionType.Factory<X> factory) {
        super(name);
        this.checked = ScopedExceptionType.build(this, factory);
    }

    public CheckedScope(Class<?> clazz, ScopedExceptionType.ScopedFactory<X> factory) {
        super(clazz);
        this.checked = ScopedExceptionType.build(this, factory);
    }

    public CheckedScope(String name, ScopedExceptionType.ScopedFactory<X> factory) {
        super(name);
        this.checked = ScopedExceptionType.build(this, factory);
    }

    public ScopedExceptionType<X> checked() {
        return checked;
    }

    @Override
    public NoValueFactory<? extends NoResult> unchecked() {
        return (s, m, c) -> checked().exception(s, m, c).unchecked();
    }
}
