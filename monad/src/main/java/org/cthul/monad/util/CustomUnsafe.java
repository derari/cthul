package org.cthul.monad.util;

import org.cthul.monad.Unsafe;

public interface CustomUnsafe<T, X extends Exception, This extends CustomUnsafe<T, X, This>> extends Unsafe<T, X> {
}
