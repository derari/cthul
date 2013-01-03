package org.cthul.strings.format;

import java.util.*;

/**
 *
 * @author Arian Treffer
 */
public abstract class MatchResultsBase extends AbstractArgs implements MatchResults {
    
    protected static Object complete(Object o) {
        if (o instanceof ConversionPattern.Intermediate) {
            o = ((ConversionPattern.Intermediate) o).complete();
        }
        return o;
    }
    
    protected static List<Object> complete(List<Object> values) {
        final List<Object> result = new ArrayList<>(values.size());
        for (Object o: values) {
            result.add(complete(o));
        }
        return result;
    }
    
    protected static <Key> Map<Key, Object> complete(Map<Key, Object> values) {
        final Map<Key, Object> result = new HashMap<>(values);
        for (Map.Entry<Key, Object> e: result.entrySet()) {
            e.setValue(complete(e.getValue()));
        }
        return result;
    }
    
}
