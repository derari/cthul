
package org.cthul.parser.grammar;

import java.util.List;
import org.cthul.parser.api.Input;
import org.cthul.parser.api.RuleKey;
import org.cthul.parser.grammar.api.RuleEval;
import org.cthul.parser.lexer.api.TokenMatcher;

/**
 * 
 * @param <I> input consumed by the grammar
 */
public interface GrammarBuilder<I extends Input<?>> {

    void addProduction(RuleKey key, RuleKey[] rule, RuleEval eval);

    void setTokenMatchers(List<TokenMatcher<? super I>> tokens);
    
    Grammar<? super I> createGrammar();
    
    GrammarBuilder<I> copy();
    
    static interface SingleLookAhead<I extends Input<?>> extends GrammarBuilder<I> {

        void addLookAhead(RuleKey key, RuleKey match, RuleEval eval);
        
    }
    
    static interface MultiLookAhead<I extends Input<?>> extends GrammarBuilder<I> {

        void addLookAhead(RuleKey key, RuleKey[] match, RuleEval eval);
        
    }
    
    static interface SingleAntiMatch<I extends Input<?>> extends GrammarBuilder<I> {

        void addAntiMatch(RuleKey key, RuleKey match, RuleEval eval);
        
    }
    
    static interface MultiAntiMatch<I extends Input<?>> extends GrammarBuilder<I> {

        void addAntiMatch(RuleKey key, RuleKey[] match, RuleEval eval);
        
    }
    
}
