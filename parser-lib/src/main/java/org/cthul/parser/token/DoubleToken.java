package org.cthul.parser.token;

/**
 *
 * @author Arian Treffer
 */
public class DoubleToken extends ObjectToken<Double> {

    @SuppressWarnings("FieldNameHidesFieldInSuperclass")
    public static final TokenFactory<Number, DoubleToken> FACTORY = 
                        new AbstractTokenFactory<Number, DoubleToken>() {
        @Override
        protected Double parse(String value) {
            return Double.valueOf(value);
        }
        @Override
        protected DoubleToken newToken(int index, String symbol, int priority, Number value, int inputIndex, int inputStart, int inputEnd, int channel) {
            return new DoubleToken(index, symbol, priority, value, inputIndex, inputStart, inputEnd, channel);
        }
        @Override
        public Class<DoubleToken> getTokenType() {
            return DoubleToken.class;
        }
        @Override
        public Class<?> getTokenValueType() {
            return Double.class;
        }
        @Override
        public Class<Number> getParameterType() {
            return Number.class;
        }
    };

    public DoubleToken(int index, String symbol, int priority, Number value, int inputIndex, int inputStart, int inputEnd, int channel) {
        super(index, symbol, priority, 
                value instanceof Double ? (Double) value : value.doubleValue(), 
                inputIndex, inputStart, inputEnd, channel);
    }

}
