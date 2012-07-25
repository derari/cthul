package org.cthul.parser;

/**
 *
 * @author Arian Treffer
 */
public abstract class AbstractParser<R> {
    
    private final Lexer lexer;
    private final Grammar grammar;

    public AbstractParser(Lexer lexer, Grammar grammer) {
        this.lexer = lexer;
        this.grammar = grammer;
    }
    
    protected R parse(String input, String startSymbol) throws NoMatchException {
        final Context context = new Context();
        return parse(context, input, startSymbol);
    }

    protected R parse(final Context context, String input, String startSymbol) throws NoMatchException {
        final TokenStream tokenStream = lexer.scan(input, context);
        return (R) grammar.parse(startSymbol, tokenStream, context);
    }
    
}
