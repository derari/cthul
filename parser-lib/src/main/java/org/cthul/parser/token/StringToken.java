package org.cthul.parser.token;

/**
 *
 * @author Arian Treffer
 */
public class StringToken extends ObjectToken<String> {

    @SuppressWarnings("FieldNameHidesFieldInSuperclass")
    public static final TokenFactory<Object, StringToken> FACTORY =
                            new AbstractTokenFactory<Object, StringToken>() {
        @Override
        protected String parse(String value) {
            return value;
        }
        @Override
        protected StringToken newToken(int index, String symbol, int priority, Object value, int inputIndex, int inputStart, int inputEnd, int channel) {
            return new StringToken(index, symbol, priority, value, inputIndex, inputStart, inputEnd, channel);
        }
        @Override
        public Class<StringToken> getTokenType() {
            return StringToken.class;
        }
        @Override
        public Class<?> getTokenValueType() {
            return String.class;
        }
        @Override
        public Class<Object> getParameterType() {
            return Object.class;
        }
    };

    public StringToken(int index, String symbol, int priority, Object value, int inputIndex, int inputStart, int inputEnd, int channel) {
        super(index, symbol, priority, String.valueOf(value), inputIndex, inputStart, inputEnd, channel);
    }
}
