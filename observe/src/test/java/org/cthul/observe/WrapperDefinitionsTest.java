package org.cthul.observe;

import java.util.function.IntSupplier;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class WrapperDefinitionsTest {

    WrapperDefinitions<Notifier> interfaces = new WrapperDefinitions<>();

    @Test
    public void testGetByType() {
        interfaces.add(IntSupplier.class, n -> () -> 1);

        int i = interfaces.wrapAs(null, IntSupplier.class, o -> {}).getAsInt();
        assertThat(i, is(1));
    }

    @Test
    public void testGetFirstUntyped() {
        interfaces.add(n -> (IntSupplier) () -> 1);
        interfaces.add(n -> (IntSupplier) () -> 2);

        int i = interfaces.wrapAs(null, IntSupplier.class, o -> {}).getAsInt();
        assertThat(i, is(1));
    }

    @Test
    public void testIgnoreLambdaTypes() {
        interfaces.add(n -> (IntSupplier) () -> 1);

        IntSupplier wrapper = interfaces.wrapAs(null, IntSupplier.class, o -> {});
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> interfaces.wrapAs(null, wrapper.getClass(), o -> {}));

        assertThat(ex.getMessage(), startsWith("class org.cthul.observe.InterfaceDefinitionsTest$$Lambda$"));
    }

}
