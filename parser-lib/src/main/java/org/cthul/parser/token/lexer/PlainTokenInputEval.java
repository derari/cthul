package org.cthul.parser.token.lexer;

import org.cthul.parser.api.Context;
import org.cthul.parser.api.StringInput;
import org.cthul.parser.lexer.api.InputEval;
import org.cthul.parser.token.Token;
import org.cthul.parser.token.TokenBuilder;

public class PlainTokenInputEval<V, T extends Token<?>> implements InputEval<T, TokenBuilder<V, T>> {

    @Override
    public T eval(Context<? extends StringInput> context, TokenBuilder<V, T> match, int start, int end) {
        return match.newToken();
    }
    
    private static final PlainTokenInputEval INSTANCE = new PlainTokenInputEval();
    
    public static <V, T extends Token<?>> PlainTokenInputEval<V,T> instance() {
        return INSTANCE;
    }
    
}
