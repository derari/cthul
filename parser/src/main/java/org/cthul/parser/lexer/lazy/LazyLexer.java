package org.cthul.parser.lexer.lazy;

import org.cthul.parser.api.Context;
import org.cthul.parser.api.StringInput;
import org.cthul.parser.lexer.LexerBase;

/**
 * All lexing is done in the token matchers, as they are called by the parser.
 */
public class LazyLexer extends LexerBase<StringInput> {

    public LazyLexer() {
    }

//    public LazyLexer(Collection<? extends InputMatcher<? super StringInput>> matchers) {
//        super(matchers);
//    }

    @Override
    public StringInput scan(Context<StringInput> context) {
        return context.getInput();
    }
    
}
