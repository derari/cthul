package org.cthul.parser.grammar.earleyx;

import java.util.List;
import org.cthul.parser.api.Input;
import org.cthul.parser.api.RuleKey;
import org.cthul.parser.grammar.Grammar;
import org.cthul.parser.grammar.GrammarBuilder;
import org.cthul.parser.grammar.GrammarBuilderBase;
import org.cthul.parser.grammar.api.RuleEval;
import org.cthul.parser.lexer.api.TokenMatcher;

public class EarleyXGrammarBuilder<I extends Input<?>> 
                extends GrammarBuilderBase<I>
                implements GrammarBuilder.SingleLookAhead<I>,
                           GrammarBuilder.SingleAntiMatch<I>{

    public EarleyXGrammarBuilder() {
    }

    public EarleyXGrammarBuilder(EarleyXGrammarBuilder<I> source) {
        super(source);
    }

    @Override
    protected void generateTokenRules(List<TokenMatcher<? super I>> tokenMatchers) {
        for (TokenMatcher<? super I> tm: tokenMatchers) {
            addRule(tm.getKey()); // new TokenRule(tm.getKey, tm);
        }
    }
    
    @Override
    public void addProduction(RuleKey key, RuleKey[] rule, RuleEval eval) {
        addRule(1); // new Production(key, rule, eval);
    }

    @Override
    public void addLookAhead(RuleKey key, RuleKey match, RuleEval eval) {
        addRule(1); // new LookAhead(key, match, eval);
    }

    @Override
    public void addAntiMatch(RuleKey key, RuleKey match, RuleEval eval) {
        addRule(1); // new AntiMatch(key, match, eval);
    }
    
    protected void addRule(Object o) {
        
    }

    @Override
    public Grammar<? super I> createGrammar() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public EarleyXGrammarBuilder<I> copy() {
        return new EarleyXGrammarBuilder<>(this);
    }
    
}
