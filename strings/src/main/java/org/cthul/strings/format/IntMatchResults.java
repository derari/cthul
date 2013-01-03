package org.cthul.strings.format;

import java.util.*;

/**
 *
 * @author Arian Treffer
 */
public class IntMatchResults extends MatchResultsBase {
    
    protected final ArrayList<Object> intResults = new ArrayList<>();

    @Override
    public void put(int i, Object o) {
        insert(intResults, i, o);
    }
    
    protected final void insert(ArrayList<Object> list, int i, Object o) {
        list.ensureCapacity(i+1);
        for (int j = list.size(); j <= i; j++) list.add(null);
        list.set(i, o);
    }

    @Override
    public void put(char c, Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void put(String s, Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object get(int i) {
        if (i >= intResults.size()) return null;
        return intResults.get(i);
    }

    @Override
    public Object get(char c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object get(String s) {
        throw new UnsupportedOperationException();
    }
    
    public List<Object> getIntResultList() {
        return complete(intResults);
    }
    
    public Map<Integer, Object> getIntResultMap() {
        final Map<Integer, Object> result = new HashMap<>();
        for (int i = 0; i < intResults.size(); i++) {
            result.put(i, complete(intResults.get(i)));
        }
        return result;
    }
    
}
