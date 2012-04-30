package org.cthul.parser;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Arian Treffer
 */
public class Context {
    
    private final Map<String, Object> values = new HashMap<>();
    
    public Object put(String key, Object value) {
        return values.put(key, value);
    }
    
    public <T> Object put(Class<T> clazz, T value) {
        return values.put(clazz.getName(), value);
    }
    
    @SuppressWarnings("unchecked")
    public <R> R get(String key) {
        return (R) values.get(key);
    }
    
    @SuppressWarnings("unchecked")
    public <T> T get(Class<T> clazz) {
        return (T) values.get(clazz.getName());
    }
    
    public Collection<Object> values() {
        return values.values();
    }
    
}
