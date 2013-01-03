package org.cthul.strings.format;

import java.util.*;

/**
 *
 * @author Arian Treffer
 */
public class SimpleMatchResults extends IntMatchResults {
    
    protected final ArrayList<Object> charResults = new ArrayList<>();
    protected final Map<String, Object> stringResults = new HashMap<>();

    @Override
    public void put(char c, Object o) {
        int i = cToI(c);
        insert(charResults, i, o);
    }

    @Override
    public void put(String s, Object o) {
        stringResults.put(s, o);
    }

    @Override
    public Object get(char c) {
        int i = cToI(c);
        if (i >= charResults.size()) return null;
        return charResults.get(i);
    }

    @Override
    public Object get(String s) {
        return stringResults.get(s);
    }
    
    public List<Object> getCharResultList() {
        return complete(intResults);
    }

    public Map<Character, Object> getCharResultMap() {
        final Map<Character, Object> result = new HashMap<>();
        for (int i = 0; i < intResults.size(); i++) {
            result.put(iToC(i), complete(charResults.get(i)));
        }
        return result;
    }
    
}
