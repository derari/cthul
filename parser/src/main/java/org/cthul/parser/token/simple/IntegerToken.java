package org.cthul.parser.token.simple;

import org.cthul.parser.token.AbstractTokenFactory;
import org.cthul.parser.token.TokenFactory;

/**
 *
 * @author Arian Treffer
 */
public class IntegerToken extends SimpleToken<Integer> {

    public static final TokenFactory<Integer, IntegerToken> FACTORY = 
                        new AbstractTokenFactory<Integer, IntegerToken>() {
        @Override
        protected Integer parse(String value) {
            return Integer.valueOf(value);
        }

        @Override
        protected IntegerToken newToken(int index, String symbol, int priority, Integer value, int inputIndex, int inputStart, int inputEnd, int channel) {
            return new IntegerToken(index, symbol, priority, value, inputIndex, inputStart, inputEnd, channel);
        }

    };

    public IntegerToken(int index, String symbol, int priority, Integer value, int inputIndex, int inputStart, int inputEnd, int channel) {
        super(index, symbol, priority, value, inputIndex, inputStart, inputEnd, channel);
    }
    
}
