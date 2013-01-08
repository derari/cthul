package org.cthul.parser.token.simple;

import org.cthul.parser.token.AbstractTokenFactory;
import org.cthul.parser.token.TokenFactory;

/**
 *
 * @author Arian Treffer
 */
public class StringToken extends SimpleToken<String> {

    public static final TokenFactory<String, StringToken> FACTORY = 
                    new AbstractTokenFactory<String, StringToken>() {

        @Override
        protected String parse(String value) {
            return value;
        }

        @Override
        protected StringToken newToken(int index, String symbol, int priority, String value, int inputIndex, int inputStart, int inputEnd, int channel) {
            return new StringToken(index, symbol, priority, value, inputIndex, inputStart, inputEnd, channel);
        }

    };

    public StringToken(int index, String symbol, int priority, String value, int inputIndex, int inputStart, int inputEnd, int channel) {
        super(index, symbol, priority, value, inputIndex, inputStart, inputEnd, channel);
    }
    
}
