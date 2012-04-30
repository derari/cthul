package org.cthul.parser;

import org.cthul.parser.annotation.*;
import org.cthul.parser.annotation.Channel;
import org.cthul.parser.annotation.Priority;
import org.cthul.parser.lexer.IntegerToken;

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
    @Match("\"(([^\"\\\\]|\\\")*)\"")
    public void STRING(@Group(1) String s, TokenBuilder tb) {
        tb.setValue(s);
    }
    
    @Priority(1)
    @Key("INT")
    @Match("[0-9]+")
    @TokenClass(IntegerToken.class)
    public void decimalInt() {}
    
    @Priority(2)
    @Match("0x([0-9A-Fa-f]+)")
    public void hexInt(@Group(1) String s, TokenBuilder tb) {
        tb.newToken("INT", IntegerToken.FACTORY, Integer.parseInt(s, 16));
    }

    @Match("[()*+-/]")
    public void operator() {}
    
    @Key
    @Match("[\\s]+")
    @Channel(Channel.Whitespace)
    public void WS() {}
    
    // GRAMMAR =================================================================
    
    @Redirect
    @Priority(P_ATOM)
    @Production({"string ::= STRING", "int ::= INT"})
    public void tokenValue() {}
    
    @Priority(P_PLUS)
    @Production("int ::= int + !int")
    public int plus(int a, Void op, int b) {
        assert op == null : "Void can not be evaluated";
        return a + b;
    }
    
    @Priority(P_MULT)
    @Production("int ::= int * !int")
    public int times(int a, Void op, Value<Integer> b) {
        if (a == 0) return 0;
        return a * b.getValue();
    }
    
    @Key("string")
    @Priority(P_MULT)
    @Production({"string * int", "int * !string"})
    public String times(@Symbol("int") int i, @Symbol("string") String s) {
        StringBuilder sb = new StringBuilder(i*s.length());
        for (int c = 0; c < i; c++) {
            sb.append(s);
        }
        return sb.toString();
    }
    
    @Priority(P_ATOM)
    @Production({"int ::= ( ?int )", "string ::= ( ?string )"})
    public Object parantheses(@Arg(1) Object value) {
        assert !(value instanceof Value) : 
                "Object should be evaluated automatically";
        return value;
    }
    
    @Priority(P_ATOM)
    @Production("int ::= - int")
    public int neg(@Arg(1) int value) {
        return - value;
    }
    
    
}
