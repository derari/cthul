package org.cthul.parser.grammar;

import java.util.Iterator;
import org.cthul.parser.ParserFactory;
import org.cthul.parser.api.Context;
import org.cthul.parser.api.Input;
import org.cthul.parser.api.RuleKey;
import org.cthul.parser.grammar.AmbiguousGrammar;
import org.cthul.parser.grammar.Grammar;
import org.cthul.parser.lexer.Lexer;

public class Ambiguous2SimpleGrammar<I extends Input<?>> implements Grammar<I> {
    protected final AmbiguousGrammar<I> ambiguousGrammar;
    protected final boolean ensureSingleResult;

    public Ambiguous2SimpleGrammar(AmbiguousGrammar<I> ambiguousGrammar, boolean ensureSingleResult) {
        this.ambiguousGrammar = ambiguousGrammar;
        this.ensureSingleResult = ensureSingleResult;
    }

    @Override
    public Object parse(Context<? extends I> context, RuleKey startSymbol, Object arg) {
        Iterator<?> it = ambiguousGrammar.parse(context, startSymbol, arg).iterator();
        Object value = it.next();
        if (ensureSingleResult && it.hasNext()) {
            throw new IllegalArgumentException("Amibguous result:\n  " + value + "\n  " + it.next());
        }
        return value;
    }
    
    public static class Factory<Parser> implements ParserFactory<Parser> {
        
        protected final ParserFactory<Parser> f;
        protected final boolean ensureSingleResult;

        public Factory(ParserFactory<Parser> f, boolean ensureSingleResult) {
            this.f = f;
            this.ensureSingleResult = ensureSingleResult;
        }

        @Override
        public <I extends Input<?>> Parser create(Lexer<? extends I> lexer, Grammar<? super I> grammar) {
            AmbiguousGrammar<? super I> ag = (AmbiguousGrammar<? super I>) grammar;
            grammar = new Ambiguous2SimpleGrammar<>(ag, ensureSingleResult);
            return f.create(lexer, grammar);
        }
        
    }
    
}
