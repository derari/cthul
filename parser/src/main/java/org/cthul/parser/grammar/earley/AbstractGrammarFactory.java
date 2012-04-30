package org.cthul.parser.grammar.earley;

import java.util.Arrays;
import org.cthul.parser.Grammar;

/**
 *
 * @author Arian Treffer
 */
public abstract class AbstractGrammarFactory implements GrammarFactory {

    @Override
    public Grammar create(Production... rules) {
        return create(Arrays.asList(rules));
    }
    
}
