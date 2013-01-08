package org.cthul.parser.token.simple;

import org.cthul.parser.token.AbstractTokenFactory;
import org.cthul.parser.token.TokenFactory;

/**
 *
 * @author Arian Treffer
 */
public class DoubleToken extends SimpleToken<Double> {

    public static final TokenFactory<Double, DoubleToken> FACTORY = 
                        new AbstractTokenFactory<Double, DoubleToken>() {
        @Override
        protected Double parse(String value) {
            return Double.valueOf(value);
        }

        @Override
        protected DoubleToken newToken(int index, String symbol, int priority, Double value, int inputIndex, int inputStart, int inputEnd, int channel) {
            return new DoubleToken(index, symbol, priority, value, inputIndex, inputStart, inputEnd, channel);
        }
        
    };

    public DoubleToken(int index, String symbol, int priority, Double value, int inputIndex, int inputStart, int inputEnd, int channel) {
        super(index, symbol, priority, value, inputIndex, inputStart, inputEnd, channel);
    }

}
