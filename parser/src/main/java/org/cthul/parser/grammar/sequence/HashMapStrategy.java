package org.cthul.parser.grammar.sequence;

import java.util.HashMap;

/**
 *
 * @author Arian Treffer
 */
public class HashMapStrategy<K, V> 
                extends MapStrategy<K, V, HashMap<? super K, ? super V>> {

    @Override
    public HashMap<K, V> newInstance() {
        return new HashMap<K, V>();
    }
    
}
