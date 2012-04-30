package org.cthul.parser.annotation;

import org.cthul.parser.Value;

/**
 *
 * @author Arian Treffer
 */
public class MathGrammar {

    private static final int P_PLUS = 10;
    private static final int P_MULT = 20;
    private static final int P_INT = 90;
    
    @Priority(P_INT)
    @Production("term ::= INT")
    public static Integer INT(Integer i) {
        return i;
    }
    
    @Priority(P_PLUS)
    @Production("term ::= term + !term")
    public static int plus(int a, Void op, int b) {
        return a+b;
    }
    
    @Priority(P_PLUS)
    @Production("term ::= term - !term")
    public static int diff(int a, Void op, int b) {
        return a-b;
    }
    
    @Priority(P_MULT)
    @Production("term ::= term * !term")
    public static int mult(int a, Void op, Value<Integer> b) {
        if (a == 0) return 0;
        return a * b.getValue();
    }
    
    @Priority(P_MULT)
    @Production("term ::= term / !term")
    public static int div(int a, Void op, int b) {
        return a / b;
    }
    
    @Priority(P_INT)
    @Production("term ::= ( ?term )")
    public static Integer brackets(Void open, Integer v, Void close) {
        return v;
    }
    
}
