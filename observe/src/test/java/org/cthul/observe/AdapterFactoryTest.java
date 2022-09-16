package org.cthul.observe;

import java.util.function.IntSupplier;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AdapterFactoryTest {

    AdapterFactory<Void> instance = new AdapterFactory<>();

    @Test
    void testGetByType() {
        instance.declare(IntSupplier.class, n -> () -> 1);

        int i = instance.create(null, IntSupplier.class).getAsInt();
        assertThat(i, is(1));
    }

    @Test
    void testGetFirstUntyped() {
        instance.declare(n -> (IntSupplier) () -> 1);
        instance.declare(n -> (IntSupplier) () -> 2);

        int i = instance.create(null, IntSupplier.class).getAsInt();
        assertThat(i, is(1));
    }

    @Test
    void testIgnoreLambdaTypes() {
        instance.declare(n -> (IntSupplier) () -> 1);

        IntSupplier supplier = instance.create(null, IntSupplier.class);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> instance.create(null, supplier.getClass()));

        assertThat(ex.getMessage(), startsWith(getClass() + "$$Lambda$"));
    }
}
