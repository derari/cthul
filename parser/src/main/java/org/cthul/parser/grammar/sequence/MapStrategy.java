package org.cthul.parser.grammar.sequence;

import java.util.Map;
import java.util.Map.Entry;

/**
 *
 * @author Arian Treffer
 */
public abstract class MapStrategy<K, V, M extends Map<? super K, ? super V>> 
                extends AbstractSequenceStrategy<Entry<? extends K, ? extends V>, M> {

    @Override
    public void add(M map, Entry<? extends K, ? extends V> element) {
        map.put(element.getKey(), element.getValue());
    }

}
