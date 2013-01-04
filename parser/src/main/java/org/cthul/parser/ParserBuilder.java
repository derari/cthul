package org.cthul.parser;

import org.cthul.parser.api.Input;
import org.cthul.parser.grammar.Grammar;
import org.cthul.parser.grammar.GrammarBuilder;
import org.cthul.parser.grammar.GrammarBuilder.MultiAntiMatch;
import org.cthul.parser.grammar.GrammarBuilder.MultiLookAhead;
import org.cthul.parser.grammar.GrammarBuilder.SingleAntiMatch;
import org.cthul.parser.grammar.GrammarBuilder.SingleLookAhead;
import org.cthul.parser.lexer.Lexer;
import org.cthul.parser.lexer.LexerBuilder;

public class ParserBuilder<Token, I extends Input<?>> {

    protected final LexerBuilder<Token, I> lexerBuilder;
    protected final GrammarBuilder<I> grammarBuilder;
    protected final SingleLookAhead<I> gbSingleLookAhead;
    protected final MultiLookAhead<I> gbMultiLookAhead;
    protected final SingleAntiMatch<I> gbSingleAntiMatch;
    protected final MultiAntiMatch<I> gbMultiAntiMatch;

    public ParserBuilder(LexerBuilder<Token, I> lexerBuilder, GrammarBuilder<I> grammarBuilder) {
        this.lexerBuilder = lexerBuilder;
        this.grammarBuilder = grammarBuilder;
        gbSingleLookAhead = cast(SingleLookAhead.class, grammarBuilder);
        gbMultiLookAhead = cast(MultiLookAhead.class, grammarBuilder);
        gbSingleAntiMatch = cast(SingleAntiMatch.class, grammarBuilder);
        gbMultiAntiMatch = cast(MultiAntiMatch.class, grammarBuilder);
    }

    public ParserBuilder(LexerBuilder<Token, I> lexerBuilder, 
                         GrammarBuilder<I> grammarBuilder, 
                         SingleLookAhead<I> gbSingleLookAhead, 
                         MultiLookAhead<I> gbMultiLookAhead, 
                         SingleAntiMatch<I> gbSingleAntiMatch, 
                         MultiAntiMatch<I> gbMultiAntiMatch) {
        this.lexerBuilder = lexerBuilder;
        this.grammarBuilder = grammarBuilder;
        this.gbSingleLookAhead = gbSingleLookAhead;
        this.gbMultiLookAhead = gbMultiLookAhead;
        this.gbSingleAntiMatch = gbSingleAntiMatch;
        this.gbMultiAntiMatch = gbMultiAntiMatch;
    }
    
    protected static <T> T cast(Class<T> clazz, Object o) {
        if (clazz.isInstance(o)) {
            return (T) o;
        } else {
            return null;
        }
    }
    
    public <Parser> Parser createParser(ParserFactory<Parser, I> factory) {
        GrammarBuilder<I> tmp = grammarBuilder.copy();
        tmp.setTokenMatchers(lexerBuilder.getTokenMatchers());
        Lexer<? extends I> lexer = lexerBuilder.createLexer();
        Grammar<? super I> grammar = grammarBuilder.createGrammar();
        return factory.create(lexer, grammar);
    }
    
}
