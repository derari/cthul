package org.cthul.parser.grammar.sequence;

import java.util.TreeMap;

/**
 *
 * @author Arian Treffer
 */
public class TreeMapStrategy<K, V> 
                extends MapStrategy<K, V, TreeMap<? super K, ? super V>> {

    @Override
    public TreeMap<K, V> newInstance() {
        return new TreeMap<K, V>();
    }
    
}
