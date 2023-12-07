package org.cthul.observe;

import org.junit.jupiter.api.Test;

import java.util.function.IntSupplier;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AdapterFactoryTest {

    AdapterFactory<Void> instance = new AdapterFactory<>();

    @Test
    void create_fromTypedDeclaration() {
        instance.declare(IntSupplier.class, n -> () -> 1);

        int i = instance.create(null, IntSupplier.class).getAsInt();
        assertThat(i, is(1));
    }

    @Test
    void create_fromFirstUntypedDeclaration() {
        instance.declare(n -> (IntSupplier) () -> 1);
        instance.declare(n -> (IntSupplier) () -> 2);

        int i = instance.create(null, IntSupplier.class).getAsInt();
        assertThat(i, is(1));
    }

    @Test
    void create_failsForLambdaClass() {
        instance.declare(n -> (IntSupplier) () -> 1);

        IntSupplier supplier = instance.create(null, IntSupplier.class);
        Class<?> lambdaCLass = supplier.getClass();

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> instance.create(null, lambdaCLass));
        assertThat(ex.getMessage(), startsWith(getClass() + "$$Lambda$"));
    }
}
