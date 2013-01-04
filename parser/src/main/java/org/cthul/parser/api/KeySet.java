package org.cthul.parser.api;

import java.util.HashSet;
import java.util.Set;
import org.cthul.parser.util.StringCache;

public class KeySet {
    
    private final StringCache cache = new StringCache();
    private final Set<String> generatedSymbols = new HashSet<>();
    
    public String cached(String s) {
        return cache.get(s);
    }
    
    public String sanatized(String s) {
        return cached(s.replace("$", "$$"));
    }
    
    public RuleKey cached(RuleKey key) {
        return cached(key.getSymbol(), key.getPriority());
    }
    
    public RuleKey cached(String key, int priority) {
        return new RuleKey(sanatized(key), priority);
    }
    
    public RuleKey sanatized(RuleKey key) {
        return sanatized(key.getSymbol(), key.getPriority());
    }
    
    public RuleKey sanatized(String key, int priority) {
        return new RuleKey(sanatized(key), priority);
    }
    
    protected String generateSymbol(String string) {
        string = cached(string);
        if (!generatedSymbols.add(string)) 
            throw new IllegalArgumentException(string);
        return string;
    }
    
    protected boolean generatedExists(String s) {
        return generatedSymbols.contains(s);
    }
    
    public String generateUniqueSymbol(String s) {
        int i = 1;
        while (generatedExists(s+i)) i++;
        return generateSymbol(s+i);
    }
    
    public String generateUniqueSymbol(String s, String suffix) {
        if (!generatedExists(s)) {
            return generateSymbol(s);
        } else {
            return generateUniqueSymbol(
                    suffix == null ? s : s + suffix);
        }
    }
    
}
