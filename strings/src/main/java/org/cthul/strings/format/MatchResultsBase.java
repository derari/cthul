package org.cthul.strings.format;

import java.util.*;
import org.cthul.strings.format.ConversionPattern.Intermediate;

/**
 *
 * @author Arian Treffer
 */
public abstract class MatchResultsBase extends AbstractArgs implements MatchResults {
    
    /**
     * Converts an {@link Intermediate} into the final value.
     * @param o
     * @return final value
     */
    protected static Object complete(Object o) {
        if (o instanceof Intermediate) {
            o = ((Intermediate) o).complete();
        }
        return o;
    }
    
    protected static List<Object> complete(Collection<?> values) {
        final List<Object> result = new ArrayList<>(values.size());
        for (Object o: values) {
            result.add(complete(o));
        }
        return result;
    }
    
    protected static <Key> Map<Key, Object> complete(Map<Key, ?> values) {
        final Map<Key, Object> result = new HashMap<>(values);
        for (Map.Entry<Key, Object> e: result.entrySet()) {
            e.setValue(complete(e.getValue()));
        }
        return result;
    }
    
}
