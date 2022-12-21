package org.cthul.monad.function;

import java.io.IOException;
import java.util.function.*;
import org.cthul.monad.ScopeTest.MyRuntimeException;
import org.cthul.monad.*;
import org.cthul.monad.error.*;
import org.cthul.monad.result.BasicScope;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
    public void testSafeChecked() {
        Supplier<Unsafe<String, IOException>> safe = testScope.safe().supplier(this::throwIOException);
        IOException ex = assertThrows(IOException.class, safe.get()::get);
        assertThat(ex.getMessage(), is("io"));
    }

    @Test
    public void testSafeConsumer() {
        Consumer<Integer> consumer = System.out::println;
        CheckedConsumer<Integer, RuntimeException> cConsumer = CheckedConsumer.from(consumer);
        Function<Integer, Unsafe<?, RuntimeException>> safe = testScope.safe().consumer(cConsumer);
        assertThat(safe.apply(1).getStatus(), is(DefaultStatus.NO_VALUE));
    }

    @Test
    public void testCheckedSafe() {
        Supplier<Integer> supplier = () -> 1;
        CheckedSupplier<Integer, RuntimeException> cSupplier = CheckedSupplier.from(supplier);
        Supplier<Unsafe<Integer, ScopedException>> safe = testScope.checked().safe().supplier(cSupplier);
        assertThat(safe.get().unchecked().get(), is(1));
    }

    @Test
    public void testUncheckedSafe() {
        Supplier<Integer> supplier = () -> 1;
        CheckedSupplier<Integer, RuntimeException> cSupplier = CheckedSupplier.from(supplier);
        Supplier<Result<Integer>> safe = testScope.unchecked().safe().supplier(cSupplier);
        assertThat(safe.get().get(), is(1));
    }

    @Test
    public void testTypedUnchecked() {
        Supplier<String> getString = MyRuntimeException.SCOPE.unchecked().wrap().supplier(this::throwIOException);
        MyRuntimeException ex = assertThrows(MyRuntimeException.class, getString::get);
        assertThat(ex.getCause(), instanceOf(IOException.class));
    }

    private String throwIOException() throws IOException {
        throw new IOException("io");
    }

    @Test
    public void testWithHandler() {
        CheckedSupplier<Integer, RuntimeException> cSupplier = () -> IllegalArgument.illegalArgument()
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
