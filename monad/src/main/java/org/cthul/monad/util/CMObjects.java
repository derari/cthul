package org.cthul.monad.util;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import org.checkerframework.checker.nullness.qual.NonNull;

public class CMObjects {
    
    public static Optional<Boolean> compareIdentityAndClass(Object o1, Object o2) {
        if (o1 == o2) return O_TRUE;
        if (o1 == null || o2 == null) return O_FALSE;
        if (o1.getClass() != o2.getClass()) return O_FALSE;
        return Optional.empty();
    }
    
    public static <T> boolean safeEquals(@NonNull T o1, Object o2, Predicate<T> equals) {
        if (o1 == o2) return true;
        if (o2 == null) return false;
        if (o1.getClass() != o2.getClass()) return false;
        return equals.test((T) o2);
    }
    
    
    public static int hashCode(int initial, int factor, Object value1, Object value2) {
        initial = initial * factor + Objects.hashCode(value1);
        initial = initial * factor + Objects.hashCode(value2);
        return initial;
    }
    
    private static final Optional<Boolean> O_TRUE = Optional.of(true);
    private static final Optional<Boolean> O_FALSE = Optional.of(false);
}
