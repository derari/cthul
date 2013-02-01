package org.cthul.parser.test;

import org.cthul.parser.ParserBuilder;
import org.cthul.parser.grammar.earleyx.EarleyXRkGrammarBuilder;
import org.cthul.parser.lexer.api.InputEval;
import org.cthul.parser.token.Token;
import org.cthul.parser.token.TokenBuilder;
import org.cthul.parser.token.TokenStream;
import org.cthul.parser.token.lexer.PlainTokenInputEval;
import org.cthul.parser.token.lexer.SimpleTokenLexerBuilder;
import org.cthul.parser.token.lexer.TokenStreamInputEval;


public class IntsAndStringsExRkTest extends IntsAndStringsTest {

    @Override
    protected ParserBuilder<?, ?> createParserBuilder() {
        return newParserBuilder();
    }
    
    protected <V,T extends Token<?>> ParserBuilder<?, ?> newParserBuilder() {
        SimpleTokenLexerBuilder<V, T> lb = new SimpleTokenLexerBuilder<>();
        EarleyXRkGrammarBuilder<TokenStream<T>> gb = new EarleyXRkGrammarBuilder<>();
        InputEval<T, TokenBuilder<V, T>> pie = PlainTokenInputEval.instance();
        InputEval<T, Object> ie = new TokenStreamInputEval<>(pie);
        return new ParserBuilder<>(lb, gb, ie);
    }

    
    
}
