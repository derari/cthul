package org.cthul.parser.grammar.earleyx;

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import org.cthul.parser.api.Input;
import org.cthul.parser.api.RuleKey;
import org.cthul.parser.grammar.Grammar;
import org.cthul.parser.grammar.GrammarBuilder;
import org.cthul.parser.grammar.GrammarBuilderBase;
import org.cthul.parser.lexer.api.InputMatcher;
import org.cthul.parser.grammar.api.RuleEval;
import org.cthul.parser.grammar.earleyx.rule.*;

public abstract class EarleyXGrammarBuilder<I extends Input<?>> 
                extends GrammarBuilderBase<I>
                implements GrammarBuilder.SingleLookAhead<I>,
                           GrammarBuilder.MultiLookAhead<I>,
                           GrammarBuilder.SingleAntiMatch<I>,
                           GrammarBuilder.MultiAntiMatch<I>,
                           GrammarBuilder.SingleNoResult<I>,
                           GrammarBuilder.MultiNoResult<I> {
    
    protected final SortedSet<RuleTemplate<I>> rules = new TreeSet<>();
    private int nextRId = 0;

    public EarleyXGrammarBuilder() {
    }

    protected EarleyXGrammarBuilder(EarleyXGrammarBuilder<I> source) {
        super(source);
        this.rules.addAll(source.rules);
        this.nextRId = source.nextRId;
    }
    
    protected int nextRId() {
        return nextRId++;
    }

    @Override
    protected void generateInputRules(List<InputMatcher<? super I>> tokenMatchers) {
        for (InputMatcher<? super I> tm: tokenMatchers) {
            addRule(new EXTokenRule<>(tm));
        }
    }
    
    @Override
    public void addProduction(RuleKey key, RuleKey[] rule, RuleEval eval) {
        addRule(new EXProduction(eval, key.getSymbol(), key.getPriority(), 
                                 RuleKey.collectSymbols(rule), 
                                 RuleKey.collectPriorities(rule)));
    }

    @Override
    public void addLookAhead(RuleKey key, RuleKey match, RuleEval eval) {
        addLookAhead(key, new RuleKey[]{match}, eval);
    }

    @Override
    public void addLookAhead(RuleKey key, RuleKey[] match, RuleEval eval) {
        addRule(new EXLookAhead(eval, key.getSymbol(), key.getPriority(), 
                                RuleKey.collectSymbols(match), 
                                RuleKey.collectPriorities(match)));
    }

    @Override
    public void addAntiMatch(RuleKey key, RuleKey match, RuleEval eval) {
        addAntiMatch(key, new RuleKey[]{match}, eval);
    }

    @Override
    public void addAntiMatch(RuleKey key, RuleKey[] match, RuleEval eval) {
        addRule(new EXAntiMatch(eval, key.getSymbol(), key.getPriority(), 
                                RuleKey.collectSymbols(match), 
                                RuleKey.collectPriorities(match)));
    }
    
    @Override
    public void addNoResult(RuleKey key, RuleKey match, RuleEval eval) {
        addAntiMatch(key, new RuleKey[]{match}, eval);
    }

    @Override
    public void addNoResult(RuleKey key, RuleKey[] match, RuleEval eval) {
        addRule(new EXNoResult(eval, key.getSymbol(), key.getPriority(), 
                                RuleKey.collectSymbols(match), 
                                RuleKey.collectPriorities(match)));
    }
    
    protected void addRule(EXRule<? super I> rule) {
        rules.add(new RuleTemplate<>(nextRId(), rule));
    }

    @Override
    public Grammar<I> createGrammar() {
        EarleyXGrammar<I> g = newGrammar();
        for (RuleTemplate<I> rt: rules) {
            g.add(rt.getRule());
        }
        return g;
    }
    
    protected abstract EarleyXGrammar<I> newGrammar();
    
    protected static class RuleTemplate<I extends Input<?>> implements Comparable<RuleTemplate<?>> {
        
        protected final int rId;
        protected final EXRule<? super I> rule;
        protected final RuleKey key;

        public RuleTemplate(int rId, EXRule<? super I> rule) {
            this.rId = rId;
            this.rule = rule;
            this.key = new RuleKey(rule.getSymbol(), rule.getPriority());
        }

        public EXRule<? super I> getRule() {
            return rule;
        }

        @Override
        public int compareTo(RuleTemplate<?> o) {
            int c = rule.getSymbol().compareTo(o.rule.getSymbol());
            if (c != 0) return c;
            c = o.rule.getPriority() - rule.getPriority();
            if (c != 0) return c;
            return rId - o.rId;
        }

        @Override
        public String toString() {
            return rule.toString();
        }
        
    }
        
}
