package org.cthul.parser;

import org.cthul.parser.annotation.AnnotationScanner;

/**
 *
 * @author Arian Treffer
 */
public class Parser<R> extends AbstractParser<R> {
    
    public static <R> Parser<R> newInstance(Object... impl) {
        AnnotationScanner a = new AnnotationScanner();
        a.addAll(impl);
        return new Parser<>(a.createLexer(), a.createGrammar());
    }

    public Parser(Lexer lexer, Grammar grammer) {
        super(lexer, grammer);
    }

    @Override
    public R parse(String input, String startSymbol) throws NoMatchException {
        return super.parse(input, startSymbol);
    }
    
}
