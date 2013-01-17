package org.cthul.matchers.proc;

import org.cthul.proc.*;
import org.junit.Before;
import org.junit.Test;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.CoreMatchers.*;
import static org.cthul.proc.Procs.*;
import static org.cthul.matchers.exceptions.CausedBy.*;
import static org.cthul.matchers.exceptions.IsThrowable.*;
import static org.cthul.matchers.proc.Returns.*;
import static org.cthul.matchers.proc.Raises.*;

/**
 * This test shows the idea of this project with some examples.
 * @author Arian Treffer
 */
public class ConceptTest {

    public static final int InitialMana = 10;
    public int mana;

    public int magicNumber() {
        return mana--;
    }

    public Proc0 magicNumber;
    public Proc0 nestedException;
    public Proc1<String> parseInt;
    public Proc2<Integer, Integer> add;

    /**
     * Set up fresh procs for each test.
     * A proc is a piece of code that can be executed.
     */
    @Before
    public void setUp() {

        mana = InitialMana;

        magicNumber = invoke(this, "magicNumber").asProc0();

        nestedException = new P0(){
            {name("nestedException");}
            @Override protected Object run() throws Throwable {
                throw new RuntimeException("runtime",
                        new IllegalArgumentException("illegal argument",
                          new IllegalAccessError("illegal access"))
                        );
            }
        };

        parseInt = new P1<String>(){
            {name("parseInt");}
            @Override protected Object run(String a) throws Throwable {
                return Integer.parseInt(a);
            }
        };

        add = new P2<Integer, Integer>(){
            {name("add"); args(2, 3);}
            @Override protected Object run(Integer a, Integer b) throws Throwable {
                return a + b;
            }
        };
    }

    /**
     * Procs can be called and their results can be tested.
     * If a proc is not called explicitly, it is executed with default arguments.
     */
    @Test
    public void test_simpleResult() {
        assertThat(magicNumber, returns(10));
        assertThat(add, returns(5));
        assertThat(add.call(1, 2), returns(3));
        assertThat(add.with(3, 4), returns(7));
        assertThat(add.with(3, 4), result(is(not(3))));
        assertThat(parseInt.call("12"), returns(12));
    }

    /**
     * To improve performance of matching, the result of a proc is cached.
     * Of course, procs can be resetted.
     * Calling a proc actually creates a new one.
     */
    @Test
    public void test_resultCaching() {
        assertThat(magicNumber, returns(10));
        assertThat(magicNumber, returns(10));
        magicNumber.retry();
        assertThat(magicNumber, returns(9));
        assertThat(magicNumber.retry(), returns(8));
        assertThat(magicNumber.call(), returns(7));
        assertThat(magicNumber, returns(8));
    }

    /**
     * The exceptions thrown by a proc can be matched as well.
     * (This is the actual prupose of this project;-)
     * There are special matchers to test the class and message of a throwable.
     */
    @Test
    public void test_exceptions() {
        Proc parseFoo = parseInt.call("foo");
        assertThat(parseFoo, raisesException());
        assertThat(parseFoo, raises(NumberFormatException.class));
        assertThat(parseFoo, not(raises(NullPointerException.class)));
        assertThat(parseFoo, raises("For input string: .*"));
        
        assertThat(magicNumber, not(raisesException()));
    }

    /**
     * The Java standard defines chaining of throwables.
     * Such chains can be matched as well.
     */
    @Test
    public void test_nestedExceptions() {
        assertThat(nestedException, raisesException());
        assertThat(nestedException, raises(RuntimeException.class));
        assertThat(nestedException, raisesException(causedBy(IllegalArgumentException.class)));
        assertThat(nestedException, raisesException(causedBy("access")));

        // of course, throwable-matchers can be used without procs as well
        Throwable t = nestedException.getException();
        assertThat(t, causedBy("argument"));
        assertThat(t, causedBy(IllegalAccessError.class));
        assertThat(t, causedBy("^illegal .*$"));
        assertThat(t, is(throwable("runtime")));
        assertThat(t, not(causedBy("runtime")));
    }

}