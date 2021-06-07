package org.cthul.monad.error;

import org.cthul.monad.result.BasicScope;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class NullValueTest {
    
    public NullValueTest() {
    }
    
    BasicScope scope = new BasicScope("test.scope");
    
    @BeforeEach
    public void setUp() {
    }

    @Test
    @SuppressWarnings("Convert2Lambda")
    public void testErrorResolving() {
       ErrorHandlerLayer layer = ErrorHandlerLayer.handle(ArgumentError.class)
                .filter(e -> e.getExpectedType() == Integer.class)
                .filter(e -> e.getParameter().equals("theValue"))
                .apply(e -> e.resolve(17));
       try (ErrorHandlingScope ctx = layer.enable()) {
           int value = failWithNull();
           assertThat(value, is(17));
       }
    }
    
    private Integer failWithNull() {
        return NullValue.check(null, ErrorHandler.getCurrent(), this, "failWithNull", "theValue", Integer.class, scope.unchecked());
    }
}
