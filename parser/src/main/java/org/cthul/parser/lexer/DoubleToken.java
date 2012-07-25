package org.cthul.parser.lexer;

import org.cthul.parser.TokenFactory;
import org.cthul.parser.lexer.AbstractTokenFactory;
import org.cthul.parser.lexer.SimpleToken;

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
        protected DoubleToken newToken(int id, String key, Double value, int start, int end, int channel) {
            return new DoubleToken(id, key, value, start, end, channel);
        }
    };
    
    public DoubleToken(int id, String key, Double value, int start, int end, int channel) {
        super(id, key, value, start, end, channel);
    }
    
}
