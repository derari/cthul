package org.cthul.parser.grammar.earley;

import org.cthul.parser.Grammar;

/**
 *
 * @author Arian Treffer
 */
public interface GrammarFactory {
    
    public Grammar create(Iterable<? extends Production> rules);
    
    public Grammar create(Production... rules);
    
}
