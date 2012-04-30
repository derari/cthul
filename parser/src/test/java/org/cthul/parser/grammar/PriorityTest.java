/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cthul.parser.grammar;

import org.cthul.parser.Priority;
import net.sf.twip.AutoTwip;
import net.sf.twip.TwiP;
import org.junit.runner.RunWith;
import org.junit.Test;
import static net.sf.twip.verify.Verify.*;
import static org.hamcrest.Matchers.*;

/**
 *
 * @author Arian Treffer
 */
@RunWith(TwiP.class)
public class PriorityTest {
    
    private static final String DefSymbol = "D";
    private static final int DefPriority = 5;
    
    @AutoTwip
    public static final TestCase[] Tests = new TestCase[]{
            t("O", 0), t("!", 0), t("?", 0), t(".", 0), t("~", 0), 
            t("D", 5), t("(", 0), t(")", 0), 
            t("!O", "O", 6), t("?O", "O", 0), t(".O", "O", 5), t("~O", "O", 0), 
            t("!D", "D", 6), t("?D", "D", 0), t(".D", "D", 5), t("~D", "D", 0),
            t("(7)D", "D", 7), t("(9O", "O", 9), 
            t("(7)(", "(", 7), t("(7))", ")", 7),
            t("Long", 0), t("!Long", "Long", 6),
        };

    /**
     * Test of parse method, of class Priority.
     */
    @Test
    public void testParse(TestCase t) {
        String[] s = new String[]{t.symbol};
        int[] p = new int[1];
        Priority.parse(s, p, DefSymbol, DefPriority);
        verifyThat(s[0], is(t.parsed));
        verifyThat(p[0], is(t.priority));
    }
    
    private static TestCase t(String symbol, int priority) {
        return new TestCase(symbol, symbol, priority);
    }
    
    private static TestCase t(String symbol, String parsed, int priority) {
        return new TestCase(symbol, parsed, priority);
    }
    
    public static class TestCase {
        private final String symbol;
        private final String parsed;
        private final int priority;
        public TestCase(String symbol, String parsed, int priority) {
            this.symbol = symbol;
            this.parsed = parsed;
            this.priority = priority;
        }

        @Override
        public String toString() {
            return symbol + " -> " + priority + " " + parsed;
        }
        
    }
}
