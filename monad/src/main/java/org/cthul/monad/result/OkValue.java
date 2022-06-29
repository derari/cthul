package org.cthul.monad.result;

import org.cthul.monad.*;
import org.cthul.monad.util.SafeStrings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OkValue<T> extends AbstractUnsafe<T, RuntimeException> implements ValueResult<T> {

    protected final T value;

    public OkValue(T value) {
        this(AnonymousScope.INSTANCE, value);
    }

    public OkValue(Status okStatus, T value) {
        this(AnonymousScope.INSTANCE, okStatus, value);
    }

    public OkValue(Scope scope, T value) {
        this(scope, DefaultStatus.OK, value);
    }

    public OkValue(Scope scope, Status okStatus, T value) {
        super(scope, okStatus);
        if (!okStatus.isOk()) {
            throw scope.uncheckedException().internal("Value requires OK status");
        }
        this.value = value;
    }

    @Override
    public T get() {
        return value;
    }

    @Override
    public String toString() {
        return SafeStrings.toString(value);
    }

    static class AnonymousScope implements Scope {

        private static final Logger LOG = LoggerFactory.getLogger(AnonymousScope.class);
        static final AnonymousScope INSTANCE = new AnonymousScope();

        @Override
        public String getName() {
            return "";
        }
    }
}
