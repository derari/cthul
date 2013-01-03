package org.cthul.parser.util;

public class IntMap<V> {
    
    private static final Object GUARD = new Object();
    
    private static final int INITIAL_CAPICITY = 64;
    private static final float LOAD_FACTOR = 3/4f;
    
    private final float loadFactor = LOAD_FACTOR;
    private int capacity = INITIAL_CAPICITY;
    private int mask = -1;
    private int size = 0;

    private int[] keys;
    private Object[] values;

    public IntMap() {
        initMap();
    }
    
    private void initMap() {
        keys = new int[capacity];
        values = new Object[capacity];
        mask = capacity-1;
    }
    
    public int size() {
        return size;
    }
    
    private boolean slotIsFree(int i) {
        Object o = values[i];
        return o == null || o == GUARD;
    }
    
    public V put(int key, V value) {
        try {
            if (value == null) return remove(key);
            int hash = hash(key);
            int index = find(key, hash);
            if (index < 0) {
                size++; 
                ensureCapacity();
                index = findInsertIndex(key, hash);
                keys[index] = key;
            }
            V oldValue = (V) values[index];
            values[index] = value;
            return oldValue;
        } catch (IllegalStateException e) {
            throw new IllegalStateException(String.valueOf(key), e);
        }
    }
    
    public V get(int key) {
        final int hash = hash(key);
        int index = find(key, hash);
        if (index < 0) return null;
        return (V) values[index];
    }
    
    public V remove(int key) {
        final int hash = hash(key);
        int index = find(key, hash);
        if (index < 0) return null;
        return removeAt(index);
    }
    
    /**
     * Finds the highest key LE `max`, 
     * for which there is a value.
     */
    public int findHighestKey(int max) {
        int highest = Integer.MIN_VALUE;
        int index = index(hash(max));
        final int maxTry = capacity;
        for (int i = 0; i < maxTry; i++) {
            if (!slotIsFree(index)) {
                int k = keys[index];
                if (k == max) return max;
                if (max > k && k > highest) highest = k;
            }
            index = nextIndex(index);
        }
        return highest;
    }
    
    /**
     * Finds the highest key GE `min`, 
     * for which there is a value.
     */
    public int findLowestKey(int min) {
        int lowest = Integer.MAX_VALUE;
        int index = index(hash(min));
        final int maxTry = capacity;
        for (int i = 0; i < maxTry; i++) {
            if (!slotIsFree(index)) {
                int k = keys[index];
                if (k == min) return min;
                if (min < k && k < lowest) lowest = k;
            }
            index = nextIndex(index);
        }
        return lowest;
    }
    
    private void ensureCapacity() {
        while (size > capacity*loadFactor) grow();
    }
    
    private void grow() {
        int[] oldKeys = keys;
        Object[] oldValues = values;
        int oldCap = capacity;
        capacity *= 2;
        initMap();
        for (int i = 0; i < oldCap; i++) {
            Object value = oldValues[i];
            if (value != null && value != GUARD) {
                int key = oldKeys[i];
                int index = findInsertIndex(key, hash(key));
                keys[index] = key;
                values[index] = value;
            }
        }
    }
    
    /**
     * Finds the slot of an existing value for `key`;
     */
    private int find(int key, int hash) {
        int index = index(hash);
        final int maxTry = capacity;
        for (int i = 0; i < maxTry; i++) {
            Object slot = values[index];
            if (slot == null) return -1;
            if (slot != GUARD) return keys[index] == key ? index : -1;
            index = nextIndex(index);
        }
        return -1;
    }
    
    /**
     * Find slot to inser key-value
     */
    private int findInsertIndex(int key, int hash) {
        int index = index(hash);
        final int maxTry = capacity;
        for (int i = 0; i < maxTry; i++) {
            if (slotIsFree(index)) return index;
            index = nextIndex(index);
        }
        throw new IllegalStateException("Free slots expected");
    }

    private V removeAt(int index) {
        size--; 
        if (size < 0) {
            throw new IllegalStateException("empty");
        }
        V oldValue = (V) values[index];
        values[index] = GUARD;
        return oldValue;
    }
    
    private int hash(int key) {
        return key ^ (key >>> 8) ^ (key >>> 16);
    }
    
    private int index(int hash) {
        return hash & mask;
    }
    
    private int nextIndex(int index) {
        // index : 0  1  2  3
        // lookup: 0  2  1  3
        index += 2;
        if (index < capacity) {
            return index;
        } else {
            return (index + 1) & mask;
        }
    }
        
}
