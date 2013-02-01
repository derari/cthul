package org.cthul.parser.test;

import org.cthul.parser.annotation.*;
import org.cthul.parser.api.Value;
import org.cthul.parser.grammar.api.Redirect;
import org.cthul.parser.token.IntegerToken;
import org.cthul.parser.token.TokenBuilder;

/**
 * This grammar tests almost every features of Cthul Parser.
 * @author Arian Treffer
 */
public class IntsAndStrings {
    
    protected static final int P_PLUS = 30;
    protected static final int P_MULT = 60;
    protected static final int P_ATOM = 90;
    
    // LEXER ===================================================================
    
    @Key
    @TokenMatch({"\"(([^\"\\\\]|\\\")*)\"",
                 "'(([^\"\\\\]|\\')*)'"})
    public void STRING(@Group(1) String s, TokenBuilder tb) {
        tb.setValue(s);
    }
    
    @Priority(1)
    @Key("INT")
    @TokenMatch("[0-9]+")
    @TokenClass(IntegerToken.class)
    public void decimalInt() {}
    
    @Priority(2)
    @TokenMatch("0x([0-9A-Fa-f]+)")
    public void hexInt(@Group(1) String s, TokenBuilder tb) {
        tb.newToken(IntegerToken.FACTORY, "INT", Integer.parseInt(s, 16));
    }

    @TokenMatch("[()*+-/]")
    public final Void operator = null;
    
    @Key
    @TokenMatch("[\\s]+")
    @Channel(Channel.Whitespace)
    public void WS() {}
    
    @Priority(1)
    @Key
    @TokenMatch("\\$\\$")
    public void FOO_TOKEN() {}
    
    // GRAMMAR =================================================================
    
    @Priority(P_ATOM)
    @Production({"string ::= STRING", "int ::= INT"})
    public final Redirect tokenValue = Redirect.instance();
    
    @Priority(P_PLUS)
    @Production("int ::= int `+` int^")
    public int plus(int a, Void op, int b) {
        assert op == null : "Void can not be evaluated";
        return a + b;
    }
    
    @Priority(P_MULT)
    @Production("int ::= int :`*` int^")
    public int times(int a, Value<Integer> b) {
        if (a == 0) return 0;
        return a * b.eval();
    }
    
    @Key("string")
    @Priority(P_MULT)
    @Production({"string `*` int^", "int `*` string^"})
    public String times(@Step("int") int i, @Step("string") String s) {
        StringBuilder sb = new StringBuilder(i*s.length());
        for (int c = 0; c < i; c++) {
            sb.append(s);
        }
        return sb.toString();
    }
    
    @Priority(P_ATOM)
    @Production({"int ::= `(` int# `)`", "string ::= `(` string# `)`"})
    public Redirect parantheses = Redirect.step(1);
    
    @Priority(P_ATOM)
    @Production("int ::= :`-` int")
    public int neg(int value) {
        return - value;
    }
    
}