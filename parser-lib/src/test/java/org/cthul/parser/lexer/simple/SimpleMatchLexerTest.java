package org.cthul.parser.lexer.simple;

import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import org.cthul.parser.api.*;
import org.cthul.parser.lexer.api.InputMatch;
import org.cthul.parser.lexer.api.InputEval;
import org.junit.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class SimpleMatchLexerTest {
    
    protected RuleKey rk(String s) {
        return new RuleKey(s, 0);
    }
    
    @Test
    public void test_scan() {
        SimpleMatchLexerBuilder<DoubleToken> lxb = new SimpleMatchLexerBuilder<>();
        Pattern dPattern = Pattern.compile("([\\d]+\\.[\\d]+);");
        lxb.addRegexToken(rk("D"), new ParseEval(), dPattern);
        lxb.addStringToken(rk("Pi"), new PiEval(), "Pi;");
        
        SimpleLexer<? extends TokenInput<DoubleToken>> lx = lxb.createLexer();
        TokenInput<DoubleToken> ti = lx.scan(Context.forString("1.0;Pi;2.5;"));
        double total = 0;
        for (DoubleToken dt: ti) {
            total += dt.eval();
        }
        assertThat(total, is(1.0+Math.PI+2.5));
    }
    
    protected class PiEval implements InputEval<DoubleToken, MatchResult> {
        @Override
        public DoubleToken eval(Context<? extends StringInput> context, MatchResult match, int start, int end) {
            return new DoubleToken(Math.PI);
        }
    }
    
    protected class ParseEval implements InputEval<DoubleToken, MatchResult> {
        @Override
        public DoubleToken eval(Context<? extends StringInput> context, MatchResult match, int start, int end) {
            double d = Double.parseDouble(match.group(1));
            return new DoubleToken(d);
        }
    }
    
    public static class DoubleToken implements InputMatch<Double> {
        
        protected double value;

        public DoubleToken(double value) {
            this.value = value;
        }

        @Override
        public Double eval() {
            return value;
        }

        @Override
        public RuleKey getKey() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int getInputStart() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int getInputEnd() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int getStartIndex() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int getEndIndex() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Double eval(Object arg) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public String getSymbol() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int getPriority() {
            throw new UnsupportedOperationException("Not supported yet.");
        }
        
    }
}
