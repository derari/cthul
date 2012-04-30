package org.cthul.parser.lexer;

import org.cthul.parser.TokenFactory;

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
        protected StringToken newToken(int id, String key, String value, int start, int end, int channel) {
            return new StringToken(id, key, value, start, end, channel);
        }
        
    };
    
    public StringToken(int id, String key, String value, int start, int end, int channel) {
        super(id, key, value, start, end, channel);
    }
    
}
