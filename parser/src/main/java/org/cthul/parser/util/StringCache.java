package org.cthul.parser.util;

import java.util.HashMap;
import java.util.Map;

public class StringCache {

    private final Map<String, String> cache = new HashMap<>();
    
    @SuppressWarnings("RedundantStringConstructorCall")
    public String get(String s) {
        String c = cache.get(s);
        if (c == null) {
            c = new String(s);
            cache.put(c, c);
        }
        return c;
    }
    
}
