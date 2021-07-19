package org.cthul.monad.function;

import java.util.function.Supplier;
import org.cthul.monad.Result;
import org.cthul.monad.ScopedException;
import org.cthul.monad.Unsafe;
import org.cthul.monad.error.ErrorHandler;
import org.cthul.monad.error.ErrorHandlerLayer;
import org.cthul.monad.error.ErrorHandlingScope;
import org.cthul.monad.error.IllegalArgument;
import org.cthul.monad.result.BasicScope;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

public class CheckedTest {

    private final BasicScope testScope = new BasicScope("TestScope");

    @Test
    public void testSafe() {
        Supplier<Integer> supplier = () -> 1;
        CheckedSupplier<Integer, RuntimeException> cSupplier = CheckedSupplier.from(supplier);
        Supplier<Unsafe<Integer, RuntimeException>> safe = testScope.safe().supplier(cSupplier);
        assertThat(safe.get().unchecked().get(), is(1));
    }

    @Test
    public void testCheckedSafe() {
        Supplier<Integer> supplier = () -> 1;
        CheckedSupplier<Integer, RuntimeException> cSupplier = CheckedSupplier.from(supplier);
        Supplier<Unsafe<Integer, ScopedException>> safe = testScope.checked().safe().supplier(cSupplier);
        assertThat(safe.get().unchecked().get(), is(1));
    }

    @Test
    public void testSafeUnchecked() {
        Supplier<Integer> supplier = () -> 1;
        CheckedSupplier<Integer, RuntimeException> cSupplier = CheckedSupplier.from(supplier);
        Supplier<Result<Integer>> safe = testScope.safe().unchecked().supplier(cSupplier);
        assertThat(safe.get().get(), is(1));
    }

    @Test
    public void testUncheckedSafe() {
        Supplier<Integer> supplier = () -> 1;
        CheckedSupplier<Integer, RuntimeException> cSupplier = CheckedSupplier.from(supplier);
        Supplier<Result<Integer>> safe = testScope.unchecked().safe().supplier(cSupplier);
        assertThat(safe.get().get(), is(1));
    }

    @Test
    public void testWithHandler() {
        CheckedSupplier<Integer, RuntimeException> cSupplier = () -> IllegalArgument.builder()
                    .operation(this, "testWithHandler")
                    .parameter("value", Integer.class)
                    .got(0, "zero", new RuntimeException("0"))
                    .handledWith(ErrorHandler.getCurrent())
                    .getResolved();
        try {
            cSupplier.get();
            fail("exception expected");
        } catch (RuntimeException ex) {
            assertThat(ex.getMessage(), is("0"));
        }
        try (ErrorHandlingScope c1 = LAYER1.enable()) {
            Integer i1 = cSupplier.get();
            assertThat(i1, is(1));
            Integer i2 = cSupplier.with(LAYER2).get();
            assertThat(i2, is(2));
        }
    }

    private static final ErrorHandlerLayer LAYER1 = ErrorHandlerLayer.handle(IllegalArgument.class, (ia, h) -> ia.resolve(1));
    private static final ErrorHandlerLayer LAYER2 = ErrorHandlerLayer.handle(IllegalArgument.class, (ia, h) -> ia.resolve(2));
}
