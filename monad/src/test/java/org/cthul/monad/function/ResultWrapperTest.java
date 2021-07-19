package org.cthul.monad.function;

import java.util.NoSuchElementException;
import org.cthul.monad.DefaultStatus;
import org.cthul.monad.Unsafe;
import org.cthul.monad.result.BasicScope;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class ResultWrapperTest {

    private final BasicScope testScope = new BasicScope("TestScope");

    @Test
    public void testWithStatus() {
        Unsafe<Integer, RuntimeException> unsafe = testScope.asResultWrapper()
                .withStatusByException()
                    .ifInstanceOf(IllegalArgumentException.class)
                        .set(DefaultStatus.BAD_REQUEST)
                    .orElse()
                        .set(DefaultStatus.INTERNAL_ERROR)
                .safe().get(() -> {
                    throw new IllegalArgumentException();
                });

        assertThat(unsafe.getStatus(), is(DefaultStatus.BAD_REQUEST));
    }

    @Test
    public void testWithStatusDefault() {
        Unsafe<Integer, RuntimeException> unsafe = testScope.asResultWrapper()
                .withStatusByException()
                    .ifInstanceOf(IllegalArgumentException.class)
                        .set(DefaultStatus.BAD_REQUEST)
                    .orElse()
                        .set(DefaultStatus.INTERNAL_ERROR)
                .safe().get(() -> {
                    throw new NoSuchElementException();
                });

        assertThat(unsafe.getStatus(), is(DefaultStatus.INTERNAL_ERROR));
    }
}
