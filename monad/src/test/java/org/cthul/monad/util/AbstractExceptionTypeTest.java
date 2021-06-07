package org.cthul.monad.util;

import java.util.function.Supplier;
import org.cthul.monad.ScopedException;
import org.cthul.monad.Unsafe;
import org.cthul.monad.function.UncheckedSupplier;
import org.cthul.monad.result.BasicScope;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;

public class AbstractExceptionTypeTest {
    
    BasicScope scope = new BasicScope("test.scope");
    
    @Test
    public void testInternalize() {
        UncheckedSupplier<String, ?> supplier = () -> { throw new IllegalArgumentException("foo"); };
        Supplier<Unsafe<String, ScopedException>> safe = scope.checked().internalize().safe().supplier(supplier);
        Unsafe<String, ScopedException> result = safe.get();
        assertThat(result.getException().getMessage(), startsWith("Internal error"));
    }
}
