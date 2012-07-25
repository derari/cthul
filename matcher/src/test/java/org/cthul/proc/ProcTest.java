package org.cthul.proc;

import org.cthul.matchers.proc.ConceptTest;
import org.junit.Test;

/**
 * See {@link ConceptTest} for more tests.
 * @author Arian Treffer
 */
public class ProcTest {
        
    @Test
    public void test() {
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

}
