package org.cthul.parser.grammar.earleyx;

import java.util.*;
import org.cthul.parser.api.*;
import org.cthul.parser.grammar.AmbiguousGrammar;
import org.cthul.parser.grammar.earleyx.algorithm.EarleyXParser;
import org.cthul.parser.grammar.earleyx.rule.EXRule;
import org.cthul.parser.grammar.earleyx.rule.EXRuleSet;
import org.cthul.parser.util.Format;

public abstract class EarleyXGrammar<I extends Input<?>> implements AmbiguousGrammar<I> {
    
    private final Map<String, EXRuleSet<I>> rules = new HashMap<>();
    
    protected void add(EXRule<? super I> p) {
        EXRuleSet<I> ps = rules.get(p.getSymbol());
        if (ps == null) {
            ps = new EXRuleSet<>();
            rules.put(p.getSymbol(), ps);
        }
        ps.add(p, p.getPriority());
    }
    
    public Iterable<EXRule<? super I>> getRules(String symbol, int precedence) {
        EXRuleSet<I> ps = rules.get(symbol);
        if (ps == null) {
            return Collections.emptyList();
        }
        return ps.getAll(precedence);
    }

    public EXRuleSet<I> getRules(String symbol) {
        return rules.get(symbol);
    }

    public abstract EarleyXParser<I> parser(Context<? extends I> context);
    
    @Override
    public Iterable<?> parse(Context<? extends I> context, RuleKey startSymbol) {
        Iterable<Match<?>> matches = parser(context).parse(startSymbol.getSymbol(), startSymbol.getPriority());
        return new ResultIterable<>(matches);
    }
        
    @Override
    public String toString() {
        return Format.join("{", ", ", "}", 1024, " ...}", rules.values());
    }

    protected static class ResultIterable<T> implements Iterable<T> {
        
        protected final Iterable<Match<T>> matches;

        @SuppressWarnings("unchecked")
        public ResultIterable(Iterable<Match<?>> matches) {
            this.matches = (Iterable) matches;
        }

        @Override
        public Iterator<T> iterator() {
            return new ResultIterator<>(matches.iterator());
        }
        
    }
    
    protected static class ResultIterator<T> implements Iterator<T> {
        
        protected final Iterator<Match<T>> matches;

        public ResultIterator(Iterator<Match<T>> matches) {
            this.matches = matches;
        }

        @Override
        public boolean hasNext() {
            return matches.hasNext();
        }

        @Override
        public T next() {
            return matches.next().eval();
        }

        @Override
        public void remove() {
            matches.remove();
        }
        
    }
    
}
