package org.cthul.parser.lexer.lazy;

import java.util.Collection;
import org.cthul.parser.api.Context;
import org.cthul.parser.api.StringInput;
import org.cthul.parser.lexer.LexerBase;
import org.cthul.parser.lexer.api.TokenMatcher;

/**
 * All lexing is done in the token matchers, as they are called by the parser.
 */
public class LazyLexer extends LexerBase<StringInput> {

    public LazyLexer(Collection<? extends TokenMatcher<? super StringInput>> matchers) {
        super(matchers);
    }

    @Override
    public StringInput scan(Context<StringInput> context) {
        return context.getInput();
    }
    
}
