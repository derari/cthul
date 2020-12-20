package org.cthul.monad.error;

import java.util.function.Supplier;
import org.cthul.monad.Result;
import org.cthul.monad.Scope;
import org.cthul.monad.ScopedException;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

public class CheckedTest {
    
    private final Scope testScope = new Scope("TestScope");
    
    @Test
    public void testSafe() {
        Supplier<Integer> supplier = () -> 1;
        Checked.Supplier<Integer, RuntimeException> cSupplier = Checked.from(supplier);
        Checked.Supplier<Integer, ScopedException> cSupplier2 = cSupplier.checked(testScope::wrapAsInternal);
        Supplier<Result<Integer>> safe = cSupplier2.safe(testScope::result);
        assertThat(safe.get().unchecked().get(), is(1));
    }
    
    @Test
    public void testSafe2() {
        Supplier<Integer> supplier = () -> 1;
        Checked.Supplier<Integer, RuntimeException> cSupplier = Checked.from(supplier);
        Supplier<Result<Integer>> safe = cSupplier.safe(testScope::result);
        assertThat(safe.get().unchecked().get(), is(1));
    }
    
    @Test
    public void testSafeUnchecked() {
        Supplier<Integer> supplier = () -> 1;
        Checked.Supplier<Integer, RuntimeException> cSupplier = Checked.from(supplier);
        Supplier<Result.Unchecked<Integer>> safe = cSupplier.safe(testScope.unchecked()::result);
        assertThat(safe.get().get(), is(1));
    }
    
    @Test
    public void testWithHandler() {
        Checked.Supplier<Integer, RuntimeException> cSupplier = () -> {
            return IllegalArgument
                    .unprocessable(Integer.class, new RuntimeException("0"))
                    .got(0).in(this, "testwithHandler", "value")
                    .handledOnce(ErrorHandler.current())
                    .getResolved();
        };
        try {
            cSupplier.get();
            fail("exception expected");
        } catch (RuntimeException ex) {
            assertThat(ex.getMessage(), is("0"));
        }
        try (ErrorContext c1 = LAYER1.enable()) {
            Integer i1 = cSupplier.get();
            assertThat(i1, is(1));
            Integer i2 = cSupplier.with(LAYER2).get();
            assertThat(i2, is(2));
        }
    }
    
    private static final ErrorHandlerLayer LAYER1 = ErrorHandlerLayer.handle(IllegalArgument.class, (ia, h) -> ia.resolve(1));
    private static final ErrorHandlerLayer LAYER2 = ErrorHandlerLayer.handle(IllegalArgument.class, (ia, h) -> ia.resolve(2));
}
