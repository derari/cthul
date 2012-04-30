package org.cthul.parser.lexer;

import org.cthul.parser.TokenFactory;

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
        protected IntegerToken newToken(int id, String key, Integer value, int start, int end, int channel) {
            return new IntegerToken(id, key, value, start, end, channel);
        }
    };
    
    public IntegerToken(int id, String key, Integer value, int start, int end, int channel) {
        super(id, key, value, start, end, channel);
    }
    
}
