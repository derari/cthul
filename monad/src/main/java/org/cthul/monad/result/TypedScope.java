package org.cthul.monad.result;

import org.cthul.monad.DefaultStatus;
import org.cthul.monad.result.OkValue.AnonymousScope;
import org.cthul.monad.util.ScopedRuntimeExceptionType;

@SuppressWarnings("LeakingThisInConstructor")
public class TypedScope<X extends RuntimeException & NoResult> extends NamedScope {

    private final ScopedRuntimeExceptionType<X> unchecked;

    public TypedScope(ScopedRuntimeExceptionType<X> type) {
        super(type.getExceptionClass());
        this.unchecked = type;
    }

    public TypedScope(ScopedRuntimeExceptionType.Factory<X> factory) {
        this(true, (Class<X>) factory.newException(AnonymousScope.INSTANCE, DefaultStatus.NO_VALUE, null, null).getClass(), factory);
    }

    private TypedScope(boolean internal, Class<X> clazz, ScopedRuntimeExceptionType.Factory<X> factory) {
        super(clazz);
        assert internal;
        this.unchecked = ScopedRuntimeExceptionType.build(this, clazz, factory);
    }

    public TypedScope(Class<?> clazz, ScopedRuntimeExceptionType.Factory<X> factory) {
        super(clazz);
        this.unchecked = ScopedRuntimeExceptionType.build(this, factory);
    }

    public TypedScope(String name, ScopedRuntimeExceptionType.Factory<X> factory) {
        super(name);
        this.unchecked = ScopedRuntimeExceptionType.build(this, factory);
    }

    public TypedScope(Class<?> clazz, ScopedRuntimeExceptionType.ScopedFactory<X> factory) {
        super(clazz);
        this.unchecked = ScopedRuntimeExceptionType.build(this, factory);
    }

    public TypedScope(String name, ScopedRuntimeExceptionType.ScopedFactory<X> factory) {
        super(name);
        this.unchecked = ScopedRuntimeExceptionType.build(this, factory);
    }

    @Override
    public ScopedRuntimeExceptionType<X> unchecked() {
        return unchecked;
    }

    @Override
    public ScopedRuntimeExceptionType<X> uncheckedException() {
        return unchecked;
    }
}
