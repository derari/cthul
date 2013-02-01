package org.cthul.parser.token;

/**
 *
 * @author Arian Treffer
 */
public class IntegerToken extends ObjectToken<Integer> {

    @SuppressWarnings("FieldNameHidesFieldInSuperclass")
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
        @Override
        public Class<IntegerToken> getTokenType() {
            return IntegerToken.class;
        }
        @Override
        public Class<?> getTokenValueType() {
            return Integer.class;
        }
        @Override
        public Class<Integer> getParameterType() {
            return Integer.class;
        }
    };

    public IntegerToken(int index, String symbol, int priority, Integer value, int inputIndex, int inputStart, int inputEnd, int channel) {
        super(index, symbol, priority, value, inputIndex, inputStart, inputEnd, channel);
    }
}
