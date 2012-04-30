package org.cthul.parser.grammar.earley;

import org.cthul.parser.Context;
import org.cthul.parser.Grammar;
import org.cthul.parser.TokenStream;

/**
 *
 * @author derari
 */
public class SingleResultEarleyGrammar implements Grammar {

    private final EarleyGrammar earley;

    public SingleResultEarleyGrammar(EarleyGrammar earley) {
        this.earley = earley;
    }
    
    @Override
    public Object parse(String startSymbol, TokenStream tokenStream, Context context) {
        ResultIterator<?> it = earley.parse(startSymbol, tokenStream, context);
        return it.next();
    }
    
}
