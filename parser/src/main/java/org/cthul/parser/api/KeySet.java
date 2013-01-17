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
    
    protected String sanatized(String s) {
        return cached(s.replace("$", "$$"));
    }
    
    protected RuleKey cached(RuleKey key) {
        return cached(key.getSymbol(), key.getPriority());
    }
    
    public RuleKey cached(String key, int priority) {
        return new SanatizedRuleKey(this, cached(key), priority);
    }
    
    public RuleKey sanatized(RuleKey key) {
        if (key instanceof SanatizedRuleKey) {
            if (((SanatizedRuleKey) key).ks != this) {
                throw new IllegalArgumentException(
                        "Key from unknown key set detected.");
            }
            return key;
        }
        return sanatized(key.getSymbol(), key.getPriority());
    }
    
    public RuleKey sanatized(String key, int priority) {
        return new SanatizedRuleKey(this, sanatized(key), priority);
    }
    
    protected String generateSymbol(String string) {
        string = string.replaceAll("[$]{2,}", "\\$");
        if (!string.contains("$")) {
            throw new IllegalArgumentException("Invalid internal token: " + string);
        }
        string = cached(string);
        if (!generatedSymbols.add(string)) {
            throw new IllegalArgumentException("Already generated: " + string);
        }
        return string;
    }
    
    protected boolean generatedExists(String s) {
        if (s.contains("$$")) {
            throw new IllegalArgumentException(
                    "Invalid '$$' in generated key '" + s + "'");
        }
        return generatedSymbols.contains(s);
    }
    
    public String generateUniqueSymbol(String s) {
        s = s.replaceAll("[$]{2,}", "\\$");
        int i = 1;
        while (generatedExists(s+i)) i++;
        return generateSymbol(s+i);
    }
    
    public String generateUniqueSymbol(String s, String suffix) {
        s = s.replaceAll("[$]{2,}", "\\$");
        if (!generatedExists(s)) {
            return generateSymbol(s);
        } else {
            return generateUniqueSymbol(
                    suffix == null ? s : s + suffix);
        }
    }
    
    protected static class SanatizedRuleKey extends RuleKey {
        
        private final KeySet ks;

        public SanatizedRuleKey(KeySet ks, String symbol, int priority) {
            super(symbol, priority);
            this.ks = ks;
        }
        
    }
    
}
