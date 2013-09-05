package org.cthul.proc;

import org.cthul.matchers.proc.ConceptTest;
import org.junit.Test;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

/**
 * See {@link ConceptTest} for more tests.
 * @author Arian Treffer
 */
public class ProcTest {
        
    @Test
    public void test_test() {
        Proc proc = new PN() {
            {name("sum");}
            @Override protected Object runN(Object[] args) throws Throwable {
                int total = 0;
                for (Object o: args)
                    total += (Integer)o;
                return total;
            }
        };
        proc.getResult();
    }
    
    @Test
    public void test_proc_error() {
        Proc proc = new PN() {
            {name("sum");}
            @Override protected Object runN(Object[] args) throws Throwable {
                throw new ProcError(UnsupportedOperationException.class, "--e--");
            }
        };
        try {
            proc.getResult();
        } catch (UnsupportedOperationException e) {
            assertThat(e.getMessage(), is("--e--"));
        }
    }

}
